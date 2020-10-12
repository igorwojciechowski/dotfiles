set fish_greeting ""

# --- Virtualenv
set -Ux VIRTUAL_ENV_DISABLE_PROMPT 1

# --- Fish colors
set fish_color_command 'brcyan'
set fish_color_error 'brred'
set fish_color_param 'brblue'
set fish_color_quote 'brgreen'
set fish_color_search_match --background='blue'
set fish_color_operator 'brgreen'
set fish_color_end 'brgreen'
set fish_color_redirection 'brgreen'
set color_prompt 'brblue'


function __prompt_user
	set fg 'brcyan'
	echo '─['(set_color $fg)'' (whoami)(set_color $color_prompt)']'
end

function __prompt_pwd
	set fg 'brcyan'
    set user (whoami)
	echo '─['(set_color $fg)'' (set_color $fg)(pwd | sed "s/\/home\/$user/~/")(set_color $color_prompt)(set_color $color_prompt)']'
end

function __prompt_git
	set fg 'green'
	set branch (git branch 2>/dev/null | grep \* | sed 's/* //')
	if [ "$branch" != "" ]
		echo '─['(set_color $fg)'' $branch(set_color $color_prompt)']'
	else
		echo ""
	end
end

function __prompt_venv
	set fg 'yellow'
	if [ "$VIRTUAL_ENV" ]
		echo '─['(set_color $fg)'' (basename $VIRTUAL_ENV) (python -V 2>&1 | grep -Po "\d.\d{0,2}.\d{0,2}")(set_color $color_prompt)']'
	else
		echo ''
	end
end

function __prompt_exit_code
	set fg 'brgreen'
	set exit_code $status
	if [ $exit_code != "0" ]
		echo '─['(set_color 'brred')''$exit_code(set_color $color_prompt)']'
	else
		echo '─['(set_color $fg)'' $exit_code(set_color $color_prompt)']'
	end
end


function fish_prompt
	set exit_code (__prompt_exit_code)
	echo (set_color $color_prompt)'┌'(__prompt_user)(__prompt_pwd)(__prompt_git)
	echo (set_color $color_prompt)'└'(__prompt_venv)$exit_code(set_color 'brblue')' '
end

alias dirsearch="python3 ~/repositories/dirsearch/dirsearch.py"
alias nightwatch="./node_modules/nightwatch/bin/nightwatch"
alias chrome="'/c/Program\ Files\ \(x86\)/Google/Chrome/Application/chrome.exe'"
