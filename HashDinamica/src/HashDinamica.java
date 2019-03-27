import java.lang.reflect.Constructor;
import java.util.function.Function;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class HashDinamica<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel>
{
	Diretorio<TIPO_DAS_CHAVES> diretorio;
	Buckets<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> buckets;
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param quantidadeMaximaDeBytesParaAChave Tamanho máximo que a chave pode gastar.
	 * @param quantidadeMaximaDeBytesParaODado Tamanho máximo que o dado pode gastar.
	 * @param construtorDaChave Construtor da chave. É necessário que a chave tenha um
	 * construtor sem parâmetros.
	 * @param construtorDoDado Construtor do dado. É necessário que o dado tenha um
	 * construtor sem parâmetros.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores aleatórios, não
	 * importando o tamanho dos valores.
	 */
	
	public HashDinamica(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado,
		Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		diretorio = new Diretorio<>(nomeDoArquivoDoDiretorio, funcaoHash);
		
		buckets = new Buckets<>(
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			quantidadeMaximaDeBytesParaAChave,
			quantidadeMaximaDeBytesParaODado,
			construtorDaChave,
			construtorDoDado);
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
	
	public boolean inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		boolean sucesso = false;

		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			int resultado = buckets.inserir(chave, dado, enderecoDoBucket);
			
			if (resultado == 0) // bucket cheio
			{
				diretorio.duplicar();
				// reorganizar os elementos dos buckets
				// inserir o elemento
				
				sucesso = true;
			}
		}
		
		return sucesso;
	}
}
