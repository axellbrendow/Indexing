import java.util.function.Function;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashDinamica<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel>
{
	Diretorio<TIPO_DAS_CHAVES> diretorio;
	Buckets<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> buckets;
	
	public HashDinamica(
			String nomeDoArquivoDoDiretorio,
			String nomeDoArquivoDosBuckets,
			int numeroDeRegistrosPorBucket,
			short quantidadeMaximaDeBytesParaAChave,
			short quantidadeMaximaDeBytesParaODado,
			Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		diretorio = new Diretorio<>(nomeDoArquivoDoDiretorio, funcaoHash);
		buckets = new Buckets<>(
				nomeDoArquivoDosBuckets,
				numeroDeRegistrosPorBucket,
				quantidadeMaximaDeBytesParaAChave,
				quantidadeMaximaDeBytesParaODado);
	}
	
	public boolean inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		boolean sucesso = false;
		
		if (dado != null)
		{
			long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
			
			if (enderecoDoBucket != -1)
			{
				int resultado = buckets.inserir(chave, dado, enderecoDoBucket);
			}
		}
		
		return sucesso;
	}
}
