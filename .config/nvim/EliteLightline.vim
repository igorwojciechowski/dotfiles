

  
  if &background == 'dark'
    
  let s:guishade0 = "#07283d"
  let s:guishade1 = "#184059"
  let s:guishade2 = "#184059"
  let s:guishade3 = "#254c66"
  let s:guishade4 = "#254c66"
  let s:guishade5 = "#36556e"
  let s:guishade6 = "#5d7c94"
  let s:guishade7 = "#86b1d1"
  let s:guiaccent0 = "#d95757"
  let s:guiaccent1 = "#317782"
  let s:guiaccent2 = "#8fbdc4"
  let s:guiaccent3 = "#469f97"
  let s:guiaccent4 = "#54c0b6"
  let s:guiaccent5 = "#489ab1"
  let s:guiaccent6 = "#187887"
  let s:guiaccent7 = "#187887"
  let s:shade0 = 23
  let s:shade1 = 24
  let s:shade2 = 24
  let s:shade3 = 60
  let s:shade4 = 60
  let s:shade5 = 66
  let s:shade6 = 103
  let s:shade7 = 146
  let s:accent0 = 174
  let s:accent1 = 67
  let s:accent2 = 152
  let s:accent3 = 73
  let s:accent4 = 116
  let s:accent5 = 73
  let s:accent6 = 31
  let s:accent7 = 31
  
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

  