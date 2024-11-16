set -x PATH /opt/homebrew/bin $PATH
fish_add_path /opt/homebrew/opt/java/bin
fish_add_path /Users/igor/Tools/codeql

starship init fish | source


set fish_greeting ""
# --- Fish colors
# set fish_color_command 'brblue'
# set fish_color_error 'brred'
# set fish_color_param 'brgreen'
# set fish_color_quote 'brcyan'
# set fish_color_search_match --background='blue'
# set fish_color_operator 'brgreen'
# set fish_color_end 'brgreen'
# set fish_color_redirection 'brgreen'
# set color_prompt 'brblack'
# set fg_color 'brcyan'

# --- Tools
set -x EZA_COLORS "xa=0;34:ur=0;34:uw=0;34:ux=0;34:ue=0;34:gr=0;34:gw=0;34:gx=0;34:tr=0;34:tw=0;34:tx=0;34:sn=0;36:uu=1;94:gu=1;94:gn=1;94:da=2;37"
set -x LS_COLORS "ex=1;36:di=0;97"

alias ll='eza -T -L 1 -l -g --icons -a'
alias lt='eza -T -L 2 -l -g --icons -a'

# --- Applications
alias venv="source ~/venvs/venv/bin/activate.fish"

# --- Git
abbr -a -g gco git checkout
abbr -a -g gs git status
abbr -a -g ga. git add .
abbr -a -g gc git commit
