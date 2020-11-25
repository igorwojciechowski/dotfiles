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

Plug 'arcticicestudio/nord-vim'
Plug 'dracula/vim',{'name':'dracula'}
Plug 'vim-airline/vim-airline'
Plug 'vim-airline/vim-airline-themes'
Plug 'scrooloose/nerdtree'
Plug 'neoclide/coc.nvim'
Plug 'davidhalter/jedi-vim'
Plug 'christoomey/vim-tmux-navigator'

call plug#end()

"
" Colorscheme
"
let g:dracula_colorterm=0
colorscheme dracula

"
" NerdTree
"
let NERDTreeShowHidden=1
map <C-t> :NERDTreeToggle<CR>

"
" Airline config
" 
let g:airline_theme='dracula'
let g:airline_powerline_fonts = 1
