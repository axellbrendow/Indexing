/*
MIT License

Copyright (c) 2019 Axell Brendow Batista Moreira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package hash_dinamica.implementacoes;

import java.util.ArrayList;
import java.util.function.Function;

import hash_dinamica.HashDinamica;
import serializaveis.IntSerializavel;
import serializaveis.StringSerializavel;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashDinamicaStringInt extends HashDinamica<StringSerializavel, IntSerializavel>
{
	protected short tamanhoMaximoEmBytesParaAsStrings;
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param tamanhoMaximoEmBytesParaAsStrings Quantidade máxima em bytes que as strings
	 * podem gastar.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	
	public HashDinamicaStringInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		short tamanhoMaximoEmBytesParaAsStrings,
		Function<StringSerializavel, Integer> funcaoHash) throws NoSuchMethodException, SecurityException
	{
		super(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			tamanhoMaximoEmBytesParaAsStrings,
			(short) Integer.BYTES,
			StringSerializavel.class.getConstructor(),
			IntSerializavel.class.getConstructor(),
			funcaoHash
		);
		
		this.tamanhoMaximoEmBytesParaAsStrings = tamanhoMaximoEmBytesParaAsStrings;
	}
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	
	public HashDinamicaStringInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		Function<StringSerializavel, Integer> funcaoHash) throws NoSuchMethodException, SecurityException
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			(short) StringSerializavel.PADRAO_TAMANHO_MAXIMO_EM_BYTES,
			funcaoHash
		);
	}
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param tamanhoMaximoEmBytesParaAsStrings Quantidade máxima em bytes que as strings
	 * podem gastar.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	
	public HashDinamicaStringInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		short tamanhoMaximoEmBytesParaAsStrings) throws NoSuchMethodException, SecurityException
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			tamanhoMaximoEmBytesParaAsStrings,
			(chave) ->
			{
				int strValue = -1;
				
				if (chave != null && chave.valor != null)
				{
					String str = chave.valor;
					int length = str.length();
					strValue = 0;
					
					// ideia extraída de:
					// https://www.ime.usp.br/~pf/algoritmos/aulas/hash.html
					for (int i = 0; i < length; i++)
					{
						strValue = strValue * 31 + str.charAt(i);
					}
					
					if (strValue < 0)
					{
						strValue += Integer.MAX_VALUE;
					}
				}
				
				return strValue;
			}
		);
	}
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	
	public HashDinamicaStringInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket) throws NoSuchMethodException, SecurityException
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			(short) StringSerializavel.PADRAO_TAMANHO_MAXIMO_EM_BYTES
		);
	}
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	
	public HashDinamicaStringInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets) throws NoSuchMethodException, SecurityException
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET
		);
	}
	
	/**
	 * Cria um objeto {@link serializaveis.StringSerializavel} com o valor
	 * recebido e com o tamanho máximo em bytes que foi recebido no
	 * construtor desta classe.
	 * 
	 * @param valor String a ser guardada no objeto.
	 * 
	 * @return um objeto {@link serializaveis.StringSerializavel} com o valor
	 * recebido e com o tamanho máximo em bytes que foi recebido no
	 * construtor desta classe.
	 */
	
	public StringSerializavel criarStringSerializavel(String valor)
	{
		return new StringSerializavel(valor, tamanhoMaximoEmBytesParaAsStrings);
	}
	
	/**
	 * Procura um registro na hash dinâmica com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return {@link java.lang.Integer#MIN_VALUE} se o registro não
	 * for encontrado; o dado correspondente à chave caso contrário.
	 */
	
	public int pesquisarDadoPelaChave(String chave)
	{
		int dado = Integer.MIN_VALUE;

		IntSerializavel intSerializavel =
			pesquisarDadoPelaChave( criarStringSerializavel(chave) );
		
		if (intSerializavel != null)
		{
			dado = intSerializavel.valor;
		}
		
		return dado;
	}
	
	/**
	 * Exclui o registro no bucket com a chave e o dado informados.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param dado Dado que corresponde à chave.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(String chave, int dado)
	{
		return excluir(criarStringSerializavel(chave), new IntSerializavel(dado));
	}
	
	/**
	 * Tenta excluir o primeiro registro com a chave informada.
	 * 
	 * @param chave Chave a ser excluída.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(String chave)
	{
		return excluir( criarStringSerializavel(chave) );
	}
	
	/**
	 * Exclui todos os registros com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 */
	
	public boolean excluirRegistrosComAChave(String chave)
	{
		return excluirRegistrosComAChave( criarStringSerializavel(chave) );
	}
	
	/**
	 * Procura todos os registros com uma chave específica e gera
	 * uma lista com os dados correspondentes a essas chaves.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return lista com os dados correspondentes às chaves.
	 */
	
	public int[] listarDadosComAChave(String chave)
	{
		ArrayList<IntSerializavel> list =
			listarDadosComAChave( criarStringSerializavel(chave) );
		
		int listSize = list.size();
		
		int[] dados = new int[listSize];
		
		for (int i = 0; i < listSize; i++)
		{
			dados[i] = list.get(i).valor;
		}
		
		return dados;
	}
	
	/**
	 * Tenta inserir a chave e o dado na hash dinâmica.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * 
	 * @return {@code true} se a chave e o dado forem inseridos.
	 * Caso contrário, {@code false}.
	 */
	
	public boolean inserir(String chave, int dado)
	{
		return inserir(criarStringSerializavel(chave), new IntSerializavel(dado));
	}
}
