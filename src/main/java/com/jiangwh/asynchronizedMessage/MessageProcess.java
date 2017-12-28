package com.jiangwh.asynchronizedMessage;

public class MessageProcess {

	PacketIdProcess process;
	
	PacketVarlidate varlidate;

	public PacketVarlidate getVarlidate() {
		return varlidate;
	}

	public void registerVarlidateResponse(PacketVarlidate varlidate) {
		this.varlidate = varlidate;
	}

	public PacketIdProcess getPacketIdProcess() {
		return process;
	}

	public void RegisterPacketProcess(PacketIdProcess process) {
		this.process = process;
	}
	
	private MessageProcess() {}

	public static MessageProcess getProcessInstance(){
		return MessageProcess.MessageProcessInstance.messageProcess;
	}
	
	static class MessageProcessInstance{
		static MessageProcess messageProcess = new MessageProcess();
	}
	
}
