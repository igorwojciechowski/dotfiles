" Cyanchrome palette for itchyny/lightline.vim.
if &background == 'dark'
  let s:guishade0 = "#042024"
  let s:guishade1 = "#062B30"
  let s:guishade2 = "#0E3A40"
  let s:guishade3 = "#123F45"
  let s:guishade4 = "#174A51"
  let s:guishade5 = "#2C666C"
  let s:guishade6 = "#52B5B7"
  let s:guishade7 = "#69DDCE"
  let s:guiaccent0 = "#FF5C8A"
  let s:guiaccent1 = "#E9C46A"
  let s:guiaccent2 = "#29D6BF"
  let s:guiaccent3 = "#69DDCE"
  let s:guiaccent4 = "#30F3D9"
  let s:guiaccent5 = "#35F0EA"
  let s:guiaccent6 = "#30F3D9"
  let s:guiaccent7 = "#29D6BF"
  let s:shade0 = 233
  let s:shade1 = 234
  let s:shade2 = 23
  let s:shade3 = 23
  let s:shade4 = 23
  let s:shade5 = 66
  let s:shade6 = 73
  let s:shade7 = 116
  let s:accent0 = 204
  let s:accent1 = 179
  let s:accent2 = 80
  let s:accent3 = 116
  let s:accent4 = 86
  let s:accent5 = 86
  let s:accent6 = 86
  let s:accent7 = 80
endif

let s:p = {'normal': {}, 'inactive': {}, 'insert': {}, 'replace': {}, 'visual': {}, 'tabline': {}}
let s:p.normal.left = [ [ s:guishade1, s:guiaccent5, s:shade1, s:accent5 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.normal.right = [ [ s:guishade1, s:guishade4, s:shade1, s:shade4 ], [ s:guishade5, s:guishade2, s:shade5, s:shade2 ] ]
let s:p.inactive.right = [ [ s:guishade1, s:guishade3, s:shade1, s:shade3 ], [ s:guishade3, s:guishade1, s:shade3, s:shade1 ] ]
let s:p.inactive.left =  [ [ s:guishade4, s:guishade1, s:shade4, s:shade1 ], [ s:guishade3, s:guishade0, s:shade3, s:shade0 ] ]
let s:p.insert.left = [ [ s:guishade1, s:guiaccent3, s:shade1, s:accent3 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.replace.left = [ [ s:guishade1, s:guiaccent0, s:shade1, s:accent0 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.visual.left = [ [ s:guishade1, s:guiaccent6, s:shade1, s:accent6 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.normal.middle = [ [ s:guishade5, s:guishade1, s:shade5, s:shade1 ] ]
let s:p.inactive.middle = [ [ s:guishade4, s:guishade1, s:shade4, s:shade1 ] ]
let s:p.tabline.left = [ [ s:guishade6, s:guishade2, s:shade6, s:shade2 ] ]
let s:p.tabline.tabsel = [ [ s:guishade6, s:guishade0, s:shade6, s:shade0 ] ]
let s:p.tabline.middle = [ [ s:guishade2, s:guishade4, s:shade2, s:shade4 ] ]
let s:p.tabline.right = copy(s:p.normal.right)
let s:p.normal.error = [ [ s:guiaccent0, s:guishade0, s:accent0, s:shade0 ] ]
let s:p.normal.warning = [ [ s:guiaccent1, s:guishade1, s:accent1, s:shade1 ] ]

let g:lightline#colorscheme#CyanchromeLightline#palette = s:p
