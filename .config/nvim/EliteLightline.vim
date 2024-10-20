if &background == 'dark'
  let s:guishade0 = "#07283D"
  let s:guishade1 = "#193C52"
  let s:guishade2 = "#2B4F67"
  let s:guishade3 = "#3D637C"
  let s:guishade4 = "#507692"
  let s:guishade5 = "#628AA7"
  let s:guishade6 = "#749DBC"
  let s:guishade7 = "#86B1D1"
  let s:guiaccent0 = "#D91717"
  let s:guiaccent1 = "#4C9DA9"
  let s:guiaccent2 = "#6BCCDB"
  let s:guiaccent3 = "#53D0A6"
  let s:guiaccent4 = "#54C0B6"
  let s:guiaccent5 = "#55ABC3"
  let s:guiaccent6 = "#1F91A3"
  let s:guiaccent7 = "#1F91A3"
  let s:shade0 = 23
  let s:shade1 = 24
  let s:shade2 = 66
  let s:shade3 = 66
  let s:shade4 = 103
  let s:shade5 = 109
  let s:shade6 = 110
  let s:shade7 = 146
  let s:accent0 = 160
  let s:accent1 = 73
  let s:accent2 = 116
  let s:accent3 = 115
  let s:accent4 = 116
  let s:accent5 = 110
  let s:accent6 = 73
  let s:accent7 = 73
endif

let s:p = {'normal': {}, 'inactive': {}, 'insert': {}, 'replace': {}, 'visual': {}, 'tabline': {}}
let s:p.normal.left = [ [ s:guishade1, s:guiaccent5, s:shade1, s:accent5 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.normal.right = [ [ s:guishade1, s:guishade4, s:shade1, s:shade4 ], [ s:guishade5, s:guishade2, s:shade5, s:shade2 ] ]
let s:p.inactive.right = [ [ s:guishade1, s:guishade3, s:shade1, s:shade3 ], [ s:guishade3, s:guishade1, s:shade3, s:shade1 ] ]
let s:p.inactive.left =  [ [ s:guishade4, s:guishade1, s:shade4, s:shade1 ], [ s:guishade3, s:guishade0, s:shade3, s:shade0 ] ]
let s:p.insert.left = [ [ s:guishade1, s:guiaccent3, s:shade1, s:accent3 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.replace.left = [ [ s:guishade1, s:guiaccent1, s:shade1, s:accent1 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.visual.left = [ [ s:guishade1, s:guiaccent6, s:shade1, s:accent6 ], [ s:guishade7, s:guishade2, s:shade7, s:shade2 ] ]
let s:p.normal.middle = [ [ s:guishade5, s:guishade1, s:shade5, s:shade1 ] ]
let s:p.inactive.middle = [ [ s:guishade4, s:guishade1, s:shade4, s:shade1 ] ]
let s:p.tabline.left = [ [ s:guishade6, s:guishade2, s:shade6, s:shade2 ] ]
let s:p.tabline.tabsel = [ [ s:guishade6, s:guishade0, s:shade6, s:shade0 ] ]
let s:p.tabline.middle = [ [ s:guishade2, s:guishade4, s:shade2, s:shade4 ] ]
let s:p.tabline.right = copy(s:p.normal.right)
let s:p.normal.error = [ [ s:guiaccent0, s:guishade0, s:accent0, s:shade0 ] ]
let s:p.normal.warning = [ [ s:guiaccent2, s:guishade1, s:accent2, s:shade1 ] ]

let g:lightline#colorscheme#Elite#palette = s:p