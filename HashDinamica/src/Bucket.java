import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 * Classe que gerencia um bucket específico.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Bucket<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel> implements Serializavel
{
	public static final int DESLOCAMENTO_CABECALHO = Byte.BYTES;
	
	private byte profundidadeLocal;
	private int numeroDeRegistrosPorBucket;
	RegistroDoIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> registroDoIndice;
	byte[] bucket;
	
	public Bucket(
		byte profundidadeLocal,
		int numeroDeRegistrosPorBucket,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado)
	{
		this.profundidadeLocal = profundidadeLocal;
		this.numeroDeRegistrosPorBucket = numeroDeRegistrosPorBucket;
		
		this.registroDoIndice =
			new RegistroDoIndice<>(
				'*', null, null,
				quantidadeMaximaDeBytesParaAChave,
				quantidadeMaximaDeBytesParaODado,
				construtorDaChave,
				construtorDoDado
			);
	}
	
	/**
	 * Calcula o tamanho em bytes que um bucket gasta.
	 * 
	 * @return o tamanho em bytes que um bucket gasta.
	 */
	
	private int obterTamanhoDeUmBucket()
	{
		return Byte.BYTES + // tamanho da profundidade local
			numeroDeRegistrosPorBucket * registroDoIndice.obterTamanhoMaximoEmBytes();
	}
	
	/**
	 * Tenta inserir a chave e o dado no bucket.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * 
	 * @return {@code false} se não for possível (bucket cheio).
	 * Caso contrário, retorna {@code true}.
	 */
	
	protected boolean inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		boolean success = false;
		int deslocamento = DESLOCAMENTO_CABECALHO;
		int tamanhoDeUmRegistro = registroDoIndice.obterTamanhoMaximoEmBytes();
		
		for (int i = 0; i < numeroDeRegistrosPorBucket; i++)
		{
			deslocamento += i * tamanhoDeUmRegistro;
			
			registroDoIndice.lerBytes(bucket, deslocamento);
			
			if (registroDoIndice.lapide == RegistroDoIndice.REGISTRO_DESATIVADO)
			{
				registroDoIndice.lapide = RegistroDoIndice.REGISTRO_ATIVADO;
				registroDoIndice.chave = chave;
				registroDoIndice.dado = dado;
				
				registroDoIndice.escreverObjeto(bucket, deslocamento);
				
				success = true;
			}
		}
		
		return success;
	}
	
	@Override
	public void escreverObjeto(RandomAccessFile correnteDeSaida)
	{
		if (correnteDeSaida != null)
		{
			byte[] bytes = obterBytes();
			
			try
			{
				correnteDeSaida.write(bytes);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public void lerObjeto(RandomAccessFile correnteDeEntrada)
	{
		if (correnteDeEntrada != null)
		{
			try
			{
				bucket = new byte[obterTamanhoDeUmBucket()];
				
				correnteDeEntrada.readFully(bucket);
				
				profundidadeLocal = bucket[0];
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public byte[] obterBytes()
	{
		return bucket;
	}

	@Override
	public void escreverObjeto(byte[] correnteDeSaida, int deslocamento)
	{
		byte[] bytes = obterBytes();
		
		System.arraycopy(bytes, 0, correnteDeSaida, deslocamento, bytes.length);
	}

	@Override
	public void lerBytes(byte[] bytes, int deslocamento)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		
		try
		{
			byteArrayInputStream.skip(deslocamento);
			
			byteArrayInputStream.read(bucket);
			
			byteArrayInputStream.close();
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}

	@Override
	public void lerBytes(byte[] bytes)
	{
		lerBytes(bytes, 0);
	}
}
