starship init fish | source

set fish_greeting ""
# --- Fish colors
set fish_color_command 'brcyan'
set fish_color_error 'brred'
set fish_color_param 'brblue'
set fish_color_quote 'brgreen'
set fish_color_search_match --background='blue'
set fish_color_operator 'brgreen'
set fish_color_end 'brgreen'
set fish_color_redirection 'brgreen'
set color_prompt 'brblack'
set fg_color 'cyan'

# --- Tools
alias ll='env EXA_COLORS="ur=0;32:uw=0;32:ux=0;32:ue=0;32:gr=0;32:gw=0;32:gx=0;32:tr=0;32:tw=0;32:tx=0;32:sn=1;36:uu=0;36" LS_COLORS="ex=1;36:di=0;36" exa -T -L 1 -l --icons -a'
alias lt='env EXA_COLORS="ur=0;32:uw=0;32:ux=0;32:ue=0;32:gr=0;32:gw=0;32:gx=0;32:tr=0;32:tw=0;32:tx=0;32:sn=1;36:uu=0;36" LS_COLORS="ex=1;36:di=0;36" exa -T -L 2 -l --icons -a'

# --- Applications
alias adb="/d/Android/platform-tools/adb.exe"
alias apktool="java -jar /d/tools/apktool_2.6.0.jar"
alias baksmali="jara -jar /d/tools/baksmali-2.5.2.jar"
alias chrome="/d/tools/chrome-win/chrome.exe"
alias jadx="/d/tools/jadx-gui-1.2.0.exe"
alias venv="source ~/venvs/venv/bin/activate.fish"

# --- Git
abbr -a -g gco git checkout
abbr -a -g gs git status
abbr -a -g ga. git add .
abbr -a -g gc git commit