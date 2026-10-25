# -*- coding: utf-8 -*-
"""ICD-O-4 (xlsx) -> app CSVs, attaching Japanese from the ICD-O-3.2 CSVs where the term is unchanged.

usage: py -I make_icdo4.py <icd-o-4 dir> <res/raw dir> <report path>
"""
import csv
import io
import re
import sys
from collections import Counter, defaultdict

sys.path.insert(0, __import__('os').path.dirname(__file__))
from xlsx import read_xlsx  # noqa: E402

src_dir, raw_dir, report_path = sys.argv[1:4]
wb = read_xlsx(src_dir + '/ICD-O-4.xlsx')
mann = read_xlsx(src_dir + '/Morphology_annexes.xlsx')
tann = read_xlsx(src_dir + '/Topography_annexes.xlsx')
report = io.open(report_path, 'w', encoding='utf-8')


def norm(s):
    return re.sub(r'\s+', ' ', (s or '').replace(' ', ' ')).strip().lower()


# term-change types whose meaning is unchanged, so the 3.2 Japanese can be reused as is
SAFE_PARTS = {'US to WHO spelling', 'WHO style change', 'Typographical error correction',
              'Change in genomic nomenclature', 'Reordering of words'}
NOS_JA = '，ＮＯＳ'
CODE_REF_JA = re.compile(r'（Ｃ[^）]*）')  # 3.2の日本語末尾の部位コード参照（例：（Ｃ71._））


def change_kind(change_type):
    parts = {p.strip() for p in change_type.split('+')}
    if parts <= SAFE_PARTS:
        return 'same'
    if 'Addition of NOS' in parts and parts - {'Addition of NOS'} <= SAFE_PARTS:
        return 'add_nos'
    if 'Removal of NOS' in parts and parts - {'Removal of NOS'} <= SAFE_PARTS:
        return 'remove_nos'
    return None


# ---------------------------------------------------------------- morphology
m3 = list(csv.reader(io.open(raw_dir + '/icd_o3_morphology.csv', encoding='utf-8')))[1:]
m3_by_code_term = {}
m3_by_term = defaultdict(set)
for code, level, en, ja, obs in m3:
    if not ja:
        continue
    m3_by_code_term[(code, norm(en))] = ja
    m3_by_term[norm(en)].add(ja)

# ICD-O-4 (code, term) -> (3.2 code, 3.2 term, kind)
m_link = {}
for r in mann['Morphology term changes'][2:]:
    if len(r) >= 7 and r[0]:
        m_link[(r[0], norm(r[2]))] = (r[3], norm(r[5]), change_kind(r[6]))
for sheet in ('Morphology code changes', 'Behaviour code changes'):
    for r in mann[sheet][2:]:
        if len(r) >= 6 and r[0]:
            same_term = norm(r[2]) == norm(r[5])
            m_link.setdefault((r[0], norm(r[2])), (r[3], norm(r[5]), 'same' if same_term else None))


def code4_to_32(code):
    # 80000/3 -> 8000/3, 801-804 などの見出しはそのまま
    m = re.match(r'^(\d{4})\d/(\d)$', code)
    return '%s/%s' % m.groups() if m else code


def strip_code_ref(ja):
    return CODE_REF_JA.sub('', ja).strip()


def morph_ja(code, en):
    key = norm(en)
    c32 = code4_to_32(code)
    if (c32, key) in m3_by_code_term:
        return strip_code_ref(m3_by_code_term[(c32, key)]), 'exact'
    link = m_link.get((code, key))
    if link:
        lc, lterm, kind = link
        ja = m3_by_code_term.get((lc, lterm))
        if ja is None and len(m3_by_term.get(lterm, ())) == 1:
            ja = next(iter(m3_by_term[lterm]))
        if ja and kind:
            ja = strip_code_ref(ja)
            if kind == 'add_nos' and 'ＮＯＳ' not in ja:
                ja += NOS_JA
            elif kind == 'remove_nos':
                if not ja.endswith(NOS_JA):
                    return '', 'nos_unclear'
                ja = ja[:-len(NOS_JA)]
            return ja, 'annex_' + kind
        return '', 'annex_unsafe' if link and not kind else 'annex_nomatch'
    if len(m3_by_term.get(key, ())) == 1:
        return strip_code_ref(next(iter(m3_by_term[key]))), 'term_only'
    return '', 'none'


stats = Counter()
morph_rows = []
for r in wb['Morphology'][2:]:
    r = r + [''] * (10 - len(r))
    code, level, term, code_ref, obs = r[0].strip(), r[1].strip(), r[2].strip(), r[3].strip(), r[4].strip()
    if not term or level == '1':
        continue
    ja, how = morph_ja(code, term)
    stats[how] += 1
    if how in ('annex_unsafe', 'nos_unclear'):
        report.write('[morph %s] %s %s %s\n' % (how, code, level, term))
    en = term
    if code_ref:
        en += ' ' + code_ref
        if ja:
            ja += '（%s）' % code_ref.strip('()')
    morph_rows.append([code, level, en, ja, '1' if obs else ''])

with io.open(raw_dir + '/icd_o4_morphology.csv', 'w', encoding='utf-8', newline='') as f:
    w = csv.writer(f, lineterminator='\n')
    w.writerow(['code', 'level', 'termEn', 'termJa', 'obsolete'])
    w.writerows(morph_rows)
report.write('MORPHOLOGY %d rows: %s\n' % (len(morph_rows), dict(stats)))

# ---------------------------------------------------------------- topography
MARKER = re.compile(r'▼[０-９0-9]+')
t3 = list(csv.reader(io.open(raw_dir + '/icd_o3_topography.csv', encoding='utf-8')))[1:]
PAREN_EN = re.compile(r'\s*\([^)]*\)\s*$')
PAREN_JA = re.compile(r'\s*（[^）]*）\s*$')
t3_full = {}
t3_base = {}
t3_term = defaultdict(set)
for code, level, ttype, ja, en, review in t3:
    if not ja or review:
        continue
    ja, en = MARKER.sub('', ja).strip(), MARKER.sub('', en).strip()
    t3_full[(code, norm(en))] = ja
    t3_term[norm(en)].add(ja)
    if PAREN_EN.search(en) and PAREN_JA.search(ja):
        t3_base[(code, norm(PAREN_EN.sub('', en)))] = PAREN_JA.sub('', ja)

t_link = {}
for r in tann['Topography term changes'][2:]:
    if len(r) >= 6 and r[0]:
        t_link[(r[0], norm(r[2]))] = (r[3], norm(r[4]), change_kind(r[5]))
for r in tann['Topography code changes'][2:]:
    if len(r) >= 5 and r[0]:
        t_link.setdefault((r[0], norm(r[2])), (r[3], norm(r[4]), 'same'))
for r in tann['Topography new codes'][2:]:
    if len(r) >= 7 and r[0]:
        t_link.setdefault((r[0], norm(r[3])), (r[4], norm(r[6]), 'same' if norm(r[3]) == norm(r[6]) else None))


def topo_ja(code, full_en, base_en):
    if (code, norm(full_en)) in t3_full:
        return t3_full[(code, norm(full_en))], 'exact'
    if base_en != full_en and (code, norm(base_en)) in t3_base:
        return t3_base[(code, norm(base_en))], 'base_only'
    link = t_link.get((code, norm(base_en)))
    if link and link[2]:
        lc, lterm, _ = link
        ja = t3_full.get((lc, lterm)) or t3_base.get((lc, lterm))
        if ja:
            return ja, 'annex'
    if len(t3_term.get(norm(full_en), ())) == 1:
        return next(iter(t3_term[norm(full_en)])), 'term_only'
    return '', 'none'


stats = Counter()
topo_rows = []
for r in wb['Topography'][2:]:
    r = r + [''] * (11 - len(r))
    code, level, term = r[0].strip(), r[1].strip(), r[2].strip()
    note, includes, excludes, other = r[3].strip(), r[8].strip(), r[9].strip(), r[10].strip()
    if not term or level in ('1', '2'):
        continue
    if level == '3':
        lv, ttype = 'category', '優先用語'
    else:
        lv, ttype = 'specific', '優先用語' if level == 'Preferred' else '同義語'
    base = '· ' + term if level == 'Bullet' else term
    extras = [x for x in (includes, excludes, other) if x]
    full = base + ''.join(' (%s)' % x for x in extras)
    ja, how = topo_ja(code, full, base)
    stats[how] += 1
    topo_rows.append([code, lv, ttype, ja, full, ''])
    if note:
        topo_rows.append([code, lv, '同義語', '', 'Note: ' + note, ''])

with io.open(raw_dir + '/icd_o4_topography.csv', 'w', encoding='utf-8', newline='') as f:
    w = csv.writer(f, lineterminator='\n')
    w.writerow(['code', 'level', 'term_type', 'japanese', 'english', 'needs_review'])
    w.writerows(topo_rows)
report.write('TOPOGRAPHY %d rows: %s\n' % (len(topo_rows), dict(stats)))
