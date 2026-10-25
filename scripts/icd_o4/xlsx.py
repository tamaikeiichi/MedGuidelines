# -*- coding: utf-8 -*-
"""Minimal stdlib xlsx reader: read_xlsx(path) -> {sheet_name: [[cell, ...], ...]}"""
import re
import zipfile
import xml.etree.ElementTree as ET

NS = {'m': 'http://schemas.openxmlformats.org/spreadsheetml/2006/main',
      'r': 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'}
REL_NS = '{http://schemas.openxmlformats.org/package/2006/relationships}'


def _col_index(ref):
    letters = re.match(r'[A-Z]+', ref).group(0)
    n = 0
    for ch in letters:
        n = n * 26 + (ord(ch) - 64)
    return n - 1


def _text(el):
    # shared string / inline string: concatenate all <t> (rich text runs)
    return ''.join(t.text or '' for t in el.iter('{%s}t' % NS['m']))


def read_xlsx(path):
    z = zipfile.ZipFile(path)
    shared = []
    if 'xl/sharedStrings.xml' in z.namelist():
        root = ET.fromstring(z.read('xl/sharedStrings.xml'))
        shared = [_text(si) for si in root.findall('m:si', NS)]
    wb = ET.fromstring(z.read('xl/workbook.xml'))
    rels = ET.fromstring(z.read('xl/_rels/workbook.xml.rels'))
    target = {r.get('Id'): r.get('Target') for r in rels.iter(REL_NS + 'Relationship')}
    out = {}
    for sh in wb.find('m:sheets', NS):
        rid = sh.get('{%s}id' % NS['r'])
        t = target[rid].lstrip('/')
        if not t.startswith('xl/'):
            t = 'xl/' + t
        root = ET.fromstring(z.read(t))
        rows = []
        for row in root.iter('{%s}row' % NS['m']):
            cells = {}
            for c in row.findall('m:c', NS):
                typ = c.get('t')
                v = c.find('m:v', NS)
                if typ == 's' and v is not None:
                    val = shared[int(v.text)]
                elif typ == 'inlineStr':
                    val = _text(c)
                else:
                    val = v.text if v is not None else ''
                cells[_col_index(c.get('r'))] = val
            if cells:
                rows.append([cells.get(i, '') for i in range(max(cells) + 1)])
            else:
                rows.append([])
        out[sh.get('name')] = rows
    return out
