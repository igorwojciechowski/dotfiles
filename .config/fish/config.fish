starship init fish | source

set -x PATH ~/Android/jadx/bin/ $PATH

set fish_greeting ""
# --- Fish colors
set fish_color_command 'yellow'
set fish_color_error 'brred'
set fish_color_param 'brgreen'
set fish_color_quote 'brcyan'
set fish_color_search_match --background='blue'
set fish_color_operator 'brgreen'
set fish_color_end 'brgreen'
set fish_color_redirection 'brgreen'
set color_prompt 'brblack'
set fg_color 'cyan'

# --- Tools
alias ll='env EXA_COLORS="ur=0;32:uw=0;32:ux=0;32:ue=0;32:gr=0;32:gw=0;32:gx=0;32:tr=0;32:tw=0;32:tx=0;32:sn=1;36:uu=0;36:gu=0;36:gn=0;36:da=0;37" LS_COLORS="ex=1;32:di=0;32" exa -T -L 1 -l -g --icons -a'
alias lt='env EXA_COLORS="ur=0;32:uw=0;32:ux=0;32:ue=0;32:gr=0;32:gw=0;32:gx=0;32:tr=0;32:tw=0;32:tx=0;32:sn=1;36:uu=0;36:gu=0;36:gn=0;36:da=0;37" LS_COLORS="ex=1;32:di=0;32" exa -T -L 2 -l -g --icons -a'

# --- Applications
alias venv="source ~/venvs/venv/bin/activate.fish"

# --- Git
abbr -a -g gco git checkout
abbr -a -g gs git status
abbr -a -g ga. git add .
abbr -a -g gc git commit
