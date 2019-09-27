/* See the project's root for license information. */

package hash.hashs;

import java.util.ArrayList;
import java.util.function.Function;

import hash.Hash;
import hash.serializaveis.IntSerializavel;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashIntInt extends Hash<IntSerializavel, IntSerializavel>
{
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
	 */
	
	public HashIntInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		Function<IntSerializavel, Integer> funcaoHash)
	{
		super(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			IntSerializavel.class,
			IntSerializavel.class,
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
	 */
	
	public HashIntInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket)
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			(chave) -> { return chave.valor; }
		);
	}
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 */
	
	public HashIntInt(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets)
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

		IntSerializavel intSerializavel =
			pesquisarDadoPelaChave(new IntSerializavel(chave));
		
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
	 * Exclui todos os registros com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 */
	
	public boolean excluirRegistrosComAChave(int chave)
	{
		return excluirRegistrosComAChave(new IntSerializavel(chave));
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
		ArrayList<IntSerializavel> list =
			listarDadosComAChave(new IntSerializavel(chave));
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
