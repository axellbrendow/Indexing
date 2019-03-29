import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
	 * Procura todos os registros com uma chave específica e gera
	 * uma lista com os dados correspondentes a essas chaves.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return lista com os dados correspondentes às chaves.
	 */
	
	public ArrayList<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES chave)
	{
		return buckets.listarDadosComAChave(chave, diretorio.obterEndereçoDoBucket(chave));
	}
	
	/**
	 * Insere todos os registros ativados de um bucket na
	 * hash dinâmica.
	 * 
	 * @param bucket Bucket com os registros a serem inseridos.
	 */
	
	public void inserirElementosDe(Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		for (int i = 0; i < buckets.numeroDeRegistrosPorBucket; i++)
		{
			RegistroDoIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> registro =
				bucket.obterRegistro(i);
			
			if (registro.lapide == RegistroDoIndice.REGISTRO_ATIVADO)
			{
				inserir(registro.chave, registro.dado);
			}
		}
	}
	
	/**
	 * Cuida do processo que precisa ser feito quando tenta-se
	 * inserir um registro num bucket que está cheio.
	 * 
	 * @param enderecoDoBucket Endereço do bucket que está cheio.
	 * @param resultado Resultado do método
	 * {@link Buckets#inserir(Serializavel, Serializavel, long)}.
	 */
	
	private void tratarBucketCheio(long enderecoDoBucket, byte resultado)
	{
		// profundidade local do bucket igual à profundidade global do diretório
		if (resultado == diretorio.obterProfundidadeGlobal())
		{
			diretorio.duplicar();
		}
		
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucketExcluido =
			buckets.resetarBucket(enderecoDoBucket);
		
		buckets.criarBucket( (byte) (resultado + 1) );
		
		inserirElementosDe(bucketExcluido);
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

		//int indiceDoPonteiroParaOBucket = diretorio.obterIndiceDoPonteiroParaOBucket(chave);
		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			byte resultado = buckets.inserir(chave, dado, enderecoDoBucket);
			
			// bucket cheio, resultado será igual à profundidade local do bucket
			if (resultado > 0)
			{
				tratarBucketCheio(enderecoDoBucket, resultado);
				
				sucesso = true;
			}
		}
		
		return sucesso;
	}
}
