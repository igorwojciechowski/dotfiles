

  
  if &background == 'dark'
    
  let s:guishade0 = "#23252e"
  let s:guishade1 = "#373a48"
  let s:guishade2 = "#4d5265"
  let s:guishade3 = "#65666d"
  let s:guishade4 = "#636982"
  let s:guishade5 = "#7d829c"
  let s:guishade6 = "#9a9eb2"
  let s:guishade7 = "#f1f1f4"
  let s:guiaccent0 = "#d41919"
  let s:guiaccent1 = "#ff8c18"
  let s:guiaccent2 = "#fea44c"
  let s:guiaccent3 = "#5ebdac"
  let s:guiaccent4 = "#49afe6"
  let s:guiaccent5 = "#367af0"
  let s:guiaccent6 = "#952ac3"
  let s:guiaccent7 = "#9755b3"
  let s:shade0 = 235
  let s:shade1 = 237
  let s:shade2 = 240
  let s:shade3 = 102
  let s:shade4 = 103
  let s:shade5 = 245
  let s:shade6 = 248
  let s:shade7 = 231
  let s:accent0 = 160
  let s:accent1 = 214
  let s:accent2 = 215
  let s:accent3 = 115
  let s:accent4 = 75
  let s:accent5 = 69
  let s:accent6 = 134
  let s:accent7 = 140
  
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

  let g:lightline#colorscheme#KaliDark#palette = lightline#colorscheme#fill(s:p)

  