set number

set hlsearch
set smartcase
set ignorecase
set incsearch
syntax on

"
" Plugins
" 
call plug#begin('~/.vim/plugged')

Plug 'drewtempelmeyer/palenight.vim'
Plug 'vim-airline/vim-airline'
Plug 'vim-airline/vim-airline-themes'

call plug#end()

" 
" Palenight Colorscheme config
"
let g:palenight_color_overrides = {
\    'black': { 'gui': '#000000', "cterm": "0", "cterm16": "0" },
\    'visual_black': { "gui": "NONE", "cterm": "NONE", "cterm16": "0" },
\    'red': { "gui": "#ff5370", "cterm": "1", "cterm16": "1" },
\    'light_red': { "gui": "#ff869a", "cterm": "1", "cterm16": "1"},
\    'blue': { "gui": "#82b1ff", "cterm": "4", "cterm16": "4" },
\    'blue_purple': { "gui": "#939ede", "cterm": "4", "cterm16": "4"},
\    'purple': { "gui": "#c792ea", "cterm": "5", "cterm16": "5" },
\    'cyan': { "gui": "#89DDFF", "cterm": "6", "cterm16": "6" },
\    'gutter_fg_grey': { "gui": "#4B5263", "cterm": "8", "cterm16": "8" },
\    'comment_grey': { "gui": "#697098", "cterm": "8", "cterm16": "8" },
\    'visual_grey': { "gui": "#3E4452", "cterm": "8", "cterm16": "8" },
\    'special_grey': { "gui": "#3B4048", "cterm": "8", "cterm16": "8" },
\    'menu_grey': { "gui": "#3E4452", "cterm": "8", "cterm16": "8" },
\    'vertsplit': { "gui": "#181A1F", "cterm": "8", "cterm16": "8" },
\    "white_mask_1": { "gui": "#333747", "cterm": "8", "cterm16": "8" },
\    "white_mask_3": { "gui": "#474b59", "cterm": "8", "cterm16": "8" },
\    "white_mask_11": { "gui": "#989aa2", "cterm": "88", "cterm16": "8" },
\}



colorscheme palenight

"
" Airline config
" 
let g:airline_theme='palenight'
let g:airline_powerline_fonts = 1
