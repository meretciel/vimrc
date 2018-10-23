" {{{
" All system-wide defaults are set in $VIMRUNTIME/debian.vim and sourced by
" the call to :runtime you can find below.  If you wish to change any of those
" settings, you should do it in this file (/etc/vim/vimrc), since debian.vim
" will be overwritten everytime an upgrade of the vim packages is performed.
" It is recommended to make changes after sourcing debian.vim since it alters
" the value of the 'compatible' option.

" This line should not be removed as it ensures that various options are
" properly set to work with the Vim-related packages available in Debian.
runtime! debian.vim

" Uncomment the next line to make Vim more Vi-compatible
" NOTE: debian.vim sets 'nocompatible'.  Setting 'compatible' changes numerous
" options, so any other options should be set AFTER setting 'compatible'.
"set compatible

" Vim5 and later versions support syntax highlighting. Uncommenting the next
" line enables syntax highlighting by default.
if has("syntax")
  syntax on
endif

" If using a dark background within the editing area and syntax highlighting
" turn on this option as well
"set background=dark

" Uncomment the following to have Vim jump to the last position when
" reopening a file
"if has("autocmd")
"  au BufReadPost * if line("'\"") > 1 && line("'\"") <= line("$") | exe "normal! g'\"" | endif
"endif

" Uncomment the following to have Vim load indentation rules and plugins
" according to the detected filetype.
"if has("autocmd")
"  filetype plugin indent on
"endif

" The following are commented out as they cause vim to behave a lot
" differently from regular Vi. They are highly recommended though.
"set showcmd		" Show (partial) command in status line.
"set showmatch		" Show matching brackets.
"set ignorecase		" Do case insensitive matching
"set smartcase		" Do smart case matching
"set incsearch		" Incremental search
"set autowrite		" Automatically save before commands like :next and :make
"set hidden		" Hide buffers when they are abandoned
"set mouse=a		" Enable mouse usage (all modes)

" }}}

"@=>
" ==============================================================================
"
"
set foldmethod=marker
"set clipboard=unnamedplus
set number
set nowrap
set ignorecase
set hlsearch
set shell=/bin/bash\ -i
colorscheme koehler
let maplocalleader=","
let mapleader=","


set iskeyword=@,48-57,_,192-255,:


" =======================
" Normal Mode Key Mapping
" ======================
nnoremap <c-n> :call RelativeNumberToggle()<cr>
nnoremap <c-a> gg<s-v><s-g>



nnoremap <tab>c :tabclose<cr>
nnoremap <tab>f :tabfirst<cr>
nnoremap <tab>n :tabnew<cr>
nnoremap <tab>e :tabe 
nnoremap <tab>L :tablast<cr>
nnoremap <tab>h gT
nnoremap <tab>l gt
nnoremap <tab>1 1gt
nnoremap <tab>2 2gt
nnoremap <tab>3 3gt
nnoremap <tab>4 4gt
nnoremap <tab>5 5gt





nnoremap zj 15j
nnoremap zk 15k
nnoremap oo o<Esc>
nnoremap OO O<Esc>
nnoremap gi gi<Esc>
nnoremap nhl :noh
nnoremap <leader>h mp*'p
nnoremap <leader>f mB*ggn

nnoremap <leader>y viwy
nnoremap <leader>Y viWy

nnoremap <leader>d ggdG

nnoremap <leader>r ciw0
nnoremap <leader>R ciW0
nnoremap <leader>sr mB:tabe __run_result<cr>
nnoremap <leader>scr mB:tabe __compile_result<cr>
nnoremap <leader>b :call RHTabBack()<cr>
nnoremap <leader>fq :bd! %<cr>


nnoremap <F5> :call RHCompile()<cr>
nnoremap <F6> :call RHRun()<cr>


function! RHTabBack()
    call RHRemoveTabByName("__compile_result")
    call RHRemoveTabByName("__run_result")
    execute "normal! 'B"

endfunction


function! RHRemoveTabByName(name)
    let bufnum = bufnr(a:name)
    let winIds = win_findbuf(bufnum)
    if bufnum && len(winIds)
        let winId  = winIds[0]
        let tabwin = win_id2tabwin(winId)
        let tabNum = tabwin[0]
        let winNum = tabwin[1]
        tabfirst
        for i in range(tabNum-1)
            tabn
        endfor
        tabclose

    endif
endfunction



function! RHCompile()
    "let isBufExists = bufexists("__compile_result")
    execute "normal! mB"
    let bufnum = bufnr("__compile_result")
    let winIds = win_findbuf(bufnum)
    if !bufnum || !len(winIds)
        silent !make > __compile_result 2>&1 
        tabe __compile_result
    else
        let winId  = winIds[0]
        let tabwin = win_id2tabwin(winId)
        let tabNum = tabwin[0]
        let winNum = tabwin[1]
        tabfirst
        for i in range(tabNum-1)
            tabn
        endfor
        tabclose
        silent !make > __compile_result 2>&1 
        tabe __compile_result


        "exe winNum . "wincmd w"
        "win_gotoid(winId)
    endif
endfunction



function! RHRun()
    silent !./__run.sh
endfunction

"function! RHRun()
"    let bufnum = bufnr("__run_result")
"    let winIds = win_findbuf(bufnum)
"    if !bufnum || !len(winIds)
"        !./__run.sh
"        tabe __run_result
"    else 
"        let winId  = winIds[0]
"        let tabwin = win_id2tabwin(winId)
"        let tabNum = tabwin[0]
"        let winNum = tabwin[1]
"        tabfirst
"        for i in range(tabNum-1)
"            tabn
"        endfor
"        tabclose
"        !rm ./__run_result
"        !./__run.sh 
"        
"        if !empty(glob("__run_result"))
"            tabe __run_result
"        else
"            5sleep
"            tabe __run_result
"        endif
"
"    endif
"endfunction


function! RelativeNumberToggle()
    if &relativenumber
        set norelativenumber
        set number
    else
        set relativenumber
    endif
endfunction


" =======================
" Insert Mode Key Mapping
" ======================
inoremap jk <Esc>

inoremap <localleader>w <c-w>
inoremap <localleader>W <esc>vBs
inoremap <c-v> <esc>"+pa

" =======================
" Visual Mode Key Mapping
" =======================

vnoremap <localleader>hd <esc>yypVr=
vnoremap <localleader>hh <esc>yyppVr=kkVr=
vnoremap <localleader>c :normal I#^M
vnoremap <localleader>u :normal 0x^M
vnoremap <c-c> "+y
vnoremap <c-v> s<esc>"+p

function! EnableStyle()
    inoremap <c-v> <esc>"+pa
    inoremap [ [ 
    inoremap ( ( 
    inoremap { { 
    inoremap ) <esc>a )
    inoremap ] <esc>a ]
    inoremap } <esc>a }
    inoremap [] []
    inoremap () ()
    inoremap {} {}
endfunction

function! DisableStyle()
    call EnableStyle()
    iunmap <c-v>
    iunmap [
    iunmap ]
    iunmap (
    iunmap )
    iunmap {
    iunmap }
    iunmap ()
    iunmap []
    iunmap {}
endfunction


"call EnableStyle()


function! PythonStyle()
    call EnableStyle()
    vnoremap <localleader>c :normal 0i#
    vnoremap <localleader>u :normal 0x
endfunction

function! CppStyle()
"   call EnableStyle()
"    call DisableStyle()
    vnoremap <leader>c :normal 0i//<cr>
    vnoremap <leader>u :normal 0xx<cr>
"   inoremap } <esc>a }<s-v>=gif}a
    inoremap {<space> {<esc>o}O
    inoremap {} {}i
endfunction


autocmd BufNewFile,BufRead *.py :call PythonStyle()
autocmd BufNewFile,BufRead *.cpp :call CppStyle()
autocmd BufNewFile,BufRead *.h :call CppStyle()



" ================================================================================

set ttyfast                     " faster redraw
set backspace=indent,eol,start

set tabstop=4           " 4 space tab
set expandtab           " use spaces for tabs
set softtabstop=4       " 4 space tab
set shiftwidth=4
set modelines=1
filetype indent on
filetype plugin on
set autoindent
set cindent
set smartindent
"set indentexpr

:inoremap # X#


set rtp+=~/Programmes/ocp-indent-vim

au BufEnter *.ml setf ocaml
au BufEnter *.mli setf ocaml
au FileType ocaml call FT_ocaml()
"function FT_ocaml()
"    set textwidth=80
"    "set colorcolumn=80
"    set shiftwidth=2
"    set tabstop=2
"    " ocp-indent with ocp-indent-vim
"    let opamshare=system("opam config var share | tr -d '\n'")
"    execute "autocmd FileType ocaml source".opamshare."/vim/syntax/ocp-indent.vim"
"    filetype indent on
"    filetype plugin indent on
"endfunction




" ================== Tmp ===================


function! LatexStyle()
    let @i='0i\codeInput{f:ls}{A}'
    let @o='0i\codeOutput{f:ls}{A}'
    let @d='0i\codeInput{\hspace{1.8em}ldwf:ls}{A}'
    let @n='A\\ '

    inoremap ,,f \begin{frame}[fragile]\frametitle{}\end{frame}<<k>>0f{a
    inoremap ,,it \begin{itemize}\item{}\end{itemize}k>>0f{a
    inoremap ,,ex \begin{exampleblock}{Examples}\end{exampleblock}k
    inoremap ,,ll \begin{lstlisting}\end{lstlisting}k

    nnoremap ,,gi :g/^In \[/execute "normal! @i"
    nnoremap ,,gd :g/^   \.\.\./execute "normal! @d"
    nnoremap ,,go :g/^Out\[/execute "normal! @o"

    vnoremap ,,bl :normal! A\\
    vnoremap <localleader>c :normal I%
    vnoremap <localleader>u :normal 0x

endfunction


autocmd BufNewFile,BufRead *.tex :call LatexStyle()





function! SourceCurrentScript()
    execute ":source %"
endfunction

nnoremap <F5> :call SourceCurrentScript()


source ~/.vimrc_utils
source ~/.vimrc_theme



function! Init()
    execute ":tabedit ~/.vimrc_theme"
    execute ":tabedit ~/.vimrc_utils"
    execute ":tabedit ~/.vimrc"
    execute ":tabnext"
endfunction




function! EnableCabbrev()
    cabbrev init call Init()
    cabbrev insf call InsertFunction()
    iabbrev :insf :call InsertFunction()
    cabbrev pre call InsertPreTag()
    cabbrev redt call InsertRedtTextTag()
    cabbrev redtext call InsertRedtTextTag()
    cabbrev header call InsertHeaderTagLineMode()
    cabbrev javacode call AsCodeBlock("java")
    cabbrev pythoncode call AsCodeBlock("python")
    cabbrev pycode call AsCodeBlock("python")
    cabbrev output call AsOutputBlock()
    cabbrev gen call Generate(0)
    cabbrev genall call Generate(1)
    cabbrev genb call GenerateBlog()
    cabbrev genblog call GenerateBlog()
    cabbrev tc call ToggleConceal()
    cabbrev hi call InsertHighlightTheme()
    cabbrev cl call GenerateCleanedFile()
    cabbrev dca call DisableCabbrev()
    cabbrev htmlred call InsertHtmlRedTag()
    cabbrev escape call EscapeHtml()
    noremap <leader>e :call EscapeHtml()<cr>
endfunction



function! DisableCabbrev()
    cunabbrev init
    cunabbrev insf
    iunabbrev :insf
    cunabbrev pre
    cunabbrev redt
    cunabbrev redtext
    cunabbrev header
    cunabbrev javacode
    cunabbrev pythoncode
    cunabbrev pycode
    cunabbrev output
    cunabbrev gen
    cunabbrev genall
    cunabbrev genb
    cunabbrev genblog
    cunabbrev tc
    cunabbrev hi
    cunabbrev cl
    cunabbrev :dca
    cunabbrev :htmlred
    cunabbrev escape
    nunmap <leader>e
endfunction


call EnableCabbrev()
