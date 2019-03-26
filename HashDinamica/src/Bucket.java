import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 * Classe que gerencia um bucket específico.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Bucket<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel> implements Serializavel
{
	private byte profundidadeLocal;
	private int numeroDeRegistrosPorBucket;
	private short quantidadeMaximaDeBytesParaAChave;
	private short quantidadeMaximaDeBytesParaODado;
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
		this.quantidadeMaximaDeBytesParaAChave = quantidadeMaximaDeBytesParaAChave;
		this.quantidadeMaximaDeBytesParaODado = quantidadeMaximaDeBytesParaODado;
		this.registroDoIndice = new RegistroDoIndice<>(construtorDaChave, construtorDoDado);
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
	public void lerBytes(byte[] bytes)
	{
		this.bucket = bytes;
	}
}
