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

Plug 'vim-airline/vim-airline'
Plug 'vim-airline/vim-airline-themes'
Plug 'scrooloose/nerdtree'
Plug 'davidhalter/jedi-vim'
Plug 'nicwest/vim-http'
Plug 'neoclide/coc.nvim', {'branch': 'release', 'do': 'yarn install --frozen-lockfile'}
Plug 'francoiscabrol/ranger.vim'
Plug 'altercation/vim-colors-solarized'

call plug#end()

"
" Colorscheme
"
let g:solarized_termcolors=256
set background=dark
colorscheme solarized

"
" NerdTree
"
let NERDTreeShowHidden=1
map <C-t> :NERDTreeToggle<CR>

"
" Airline config
" 
let g:airline_theme='solarized'
let g:airline_powerline_fonts = 1
