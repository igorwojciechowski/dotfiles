

  
  if &background == 'dark'
    
  let s:guishade0 = "#113547"
  let s:guishade1 = "#294959"
  let s:guishade2 = "#415d6c"
  let s:guishade3 = "#58727e"
  let s:guishade4 = "#708691"
  let s:guishade5 = "#889aa3"
  let s:guishade6 = "#a0aeb5"
  let s:guishade7 = "#b8c2c8"
  let s:guiaccent0 = "#cc5a5a"
  let s:guiaccent1 = "#5acc97"
  let s:guiaccent2 = "#9bffd1"
  let s:guiaccent3 = "#439971"
  let s:guiaccent4 = "#5acc97"
  let s:guiaccent5 = "#48768d"
  let s:guiaccent6 = "#1d4e66"
  let s:guiaccent7 = "#22698c"
  let s:shade0 = 23
  let s:shade1 = 60
  let s:shade2 = 66
  let s:shade3 = 242
  let s:shade4 = 109
  let s:shade5 = 145
  let s:shade6 = 248
  let s:shade7 = 250
  let s:accent0 = 174
  let s:accent1 = 115
  let s:accent2 = 158
  let s:accent3 = 72
  let s:accent4 = 115
  let s:accent5 = 67
  let s:accent6 = 66
  let s:accent7 = 67
  
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

  let g:lightline#colorscheme#elite#palette = lightline#colorscheme#fill(s:p)

  