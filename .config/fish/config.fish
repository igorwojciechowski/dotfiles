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
set color_prompt 'brblack'
set fg_color 'cyan'

function __prompt_user
	echo '─['(set_color $fg_color)'' (whoami)(set_color $color_prompt)']'
end

function __prompt_pwd
    set user (whoami)
	echo '─['(set_color $fg_color)'' (set_color $fg_color)(pwd | sed "s/\/home\/$user/~/")(set_color $color_prompt)(set_color $color_prompt)']'
end

function __prompt_tun_ip
  set ip (ip addr show tun0 2>/dev/null | grep -oP "(?<=inet )[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}")
  if [ $status != "1" ]
    echo '─['(set_color $fg_color)' '$ip(set_color $color_prompt)']'
  else
    echo ''
  end
end

function __prompt_git
	set clean 'green'
	set modified 'red'
	set branch (git branch 2>/dev/null | grep \* | sed 's/* //')
	if [ "$branch" != "" ]
		set status_porcelain (git status --porcelain)
		if [ "$status_porcelain" != "" ]
			echo '─['(set_color $modified)'' $branch(set_color $color_prompt)']'
		else
			echo '─['(set_color $clean)'' $branch(set_color $color_prompt)']'
		end		
	else
		echo ""
	end
end

function __prompt_venv
	set fg 'brcyan'
	set python_version (python -V 2>&1 | grep -Po "\d.\d{0,2}.\d{0,2}")
	if [ "$VIRTUAL_ENV" ]
		echo '─['(set_color $fg)'' $python_version(set_color $color_prompt)']'
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
	echo (set_color $color_prompt)'┌'(__prompt_user)(__prompt_tun_ip)(__prompt_pwd)(__prompt_git)
	echo (set_color $color_prompt)'└'(__prompt_venv)$exit_code(set_color 'brblack')' '
end
