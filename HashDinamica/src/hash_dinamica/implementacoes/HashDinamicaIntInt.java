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

import hash_dinamica.HashDinamica;
import hash_dinamica.serializaveis.IntSerializavel;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashDinamicaIntInt extends HashDinamica<IntSerializavel, IntSerializavel>
{
	public static final int PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET = 21;
	
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
	
	public HashDinamicaIntInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket
		/*Function<IntSerializavel, Integer> funcaoHash*/) throws NoSuchMethodException, SecurityException
	{
		super(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			(short) Integer.BYTES,
			(short) Integer.BYTES,
			IntSerializavel.class.getConstructor(),
			IntSerializavel.class.getConstructor(),
			(chave) -> { return chave.valor; }
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
	
	public HashDinamicaIntInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets
		/*Function<IntSerializavel, Integer> funcaoHash*/) throws NoSuchMethodException, SecurityException
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET
		);
	}
	
	/**
	 * Procura um registro na hash dinâmica com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return {@link java.lang.Integer#MIN_VALUE} se o registro não
	 * for encontrado; o dado correspondente à chave caso contrário.
	 */
	
	public int pesquisarDadoPelaChave(int chave)
	{
		int dado = Integer.MIN_VALUE;

		IntSerializavel intSerializavel = pesquisarDadoPelaChave(new IntSerializavel(chave));
		
		if (intSerializavel != null)
		{
			dado = intSerializavel.valor;
		}
		
		return dado;
	}
	
	/**
	 * Procura um registro na hash dinâmica com o dado informado.
	 * 
	 * @param dado Dado a ser procurado.
	 * 
	 * @return {@link java.lang.Integer#MIN_VALUE} se o registro não
	 * for encontrado; a chave correspondente ao dado caso contrário.
	 */
	
	public int pesquisarChavePeloDado(int dado)
	{
		int chave = Integer.MIN_VALUE;

		IntSerializavel intSerializavel = pesquisarChavePeloDado(new IntSerializavel(dado));
		
		if (intSerializavel != null)
		{
			chave = intSerializavel.valor;
		}
		
		return chave;
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
	
	public boolean excluir(int chave, int dado)
	{
		return excluir(new IntSerializavel(chave), new IntSerializavel(dado));
	}
	
	/**
	 * Tenta excluir o primeiro registro com a chave informada.
	 * 
	 * @param chave Chave a ser excluída.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(int chave)
	{
		return excluir(new IntSerializavel(chave));
	}
	
	/**
	 * Procura todos os registros com uma chave específica e gera
	 * uma lista com os dados correspondentes a essas chaves.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return lista com os dados correspondentes às chaves.
	 */
	
	public int[] listarDadosComAChave(int chave)
	{
		ArrayList<IntSerializavel> list = listarDadosComAChave(new IntSerializavel(chave));
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
	
	public boolean inserir(int chave, int dado)
	{
		return inserir(new IntSerializavel(chave), new IntSerializavel(dado));
	}
}
