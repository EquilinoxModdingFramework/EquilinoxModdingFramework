package kd.equilinox.agent;

import java.lang.instrument.Instrumentation;

import kd.equilinox.utils.Logger;

/**
 * 
 * Agent which will run before the game main method is called.
 * 
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 *
 */
public class EquilinoxAgent {
	public static void premain(String args, Instrumentation instrumentation) {
		Logger.info("Starting agent...");
	}
}
