# ICD-O-4 マスタ変換スクリプト

IARC が配布する ICD-O-4 の Excel から、アプリ用の CSV を作ります。

- 入力: `ICD-O-4.xlsx`, `Morphology_annexes.xlsx`, `Topography_annexes.xlsx`（同じフォルダに置く）
- 出力: `app/src/main/res/raw/icd_o4_morphology.csv`, `icd_o4_topography.csv`
- 日本語は `icd_o3_morphology.csv` / `icd_o3_topography.csv`（ICD-O-3.2）から付けます。
  用語が同じもの、付録で綴り・表記・語順だけの変更とされたものは 3.2 の日本語を使い、
  NOS の追加・削除は「，ＮＯＳ」を足す／外します。それ以外は英語のままです。

## 使い方

標準ライブラリだけで動きます（openpyxl 不要）。

```
py -I scripts/icd_o4/make_icdo4.py <ICD-O-4のフォルダ> app/src/main/res/raw <レポートの出力先>
```

レポートには、日本語の付け方ごとの件数と、意味が変わりうるため英語のままにした用語が出ます。

CSV を差し替えたら、アプリは次回起動時に自動で取り込み直します（リソース名が変わった場合）。
同じファイル名のまま中身だけ変えたときは、CSV のファイル名を変えるか、アプリのデータを消してください。
