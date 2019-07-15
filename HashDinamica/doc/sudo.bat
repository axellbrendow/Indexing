for /f "usebackq" %%c in (`hostname`) do (

	set currentHostname="%%c"
)

set administrator="test"

if %1 EQU "-adm" (

	:: shift empurra os argumentos do comando para a esquerda
	shift 
	set administrator="%1"
	shift
)

runas /user:%currentHostname%\%administrator% "%1"
