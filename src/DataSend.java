import java.util.List;

public class DataSend<T> {
	
	public String command;
	public List<T> data;
	
	public DataSend(String command,List<T> data) {
		this.command = command;
		this.data = data;
	}

}
