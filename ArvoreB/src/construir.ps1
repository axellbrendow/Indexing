# Cria o diretório de construção
rm -r ../build
cp -r . ../build/ArvoreB
mv ../build/ArvoreB/test.cpp ../build/test.cpp
ls -R ../build -Exclude *.hpp, *.cpp | where { !$_.PSIsContainer } | rm
# Caso você esteja num sistema Unix, substitua a linha acima por:
# find ../build/ -type f \( ! -name "*.hpp" -and ! -name "*.cpp" \) -exec rm {} \;

# Cria os objetos binários (arquivos .o)
g++ mytest.cpp -c

# Cria a DLL
g++ mytest.o -shared -o ../build/ArvoreB.dll

# Compila o teste com a DLL e gera o executável
g++ ../build/test.cpp -L ../build -l ../build/ArvoreB -o test.exe

# Executa o teste
./test.exe

# Remove arquivos desnecessários
rm *.exe
rm ./TesteArvore.txt
rm *.stackdump
rm *.o
