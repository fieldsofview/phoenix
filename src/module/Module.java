/**
 * 
 */
package module;

/**
 * @author onkar
 * @version 0.1
 *Date of creation: 1 April 2013
 *This is the module interface. All Phoenix modules must implement this
 *interface. Custom methods for each module must be provided by the
 *module.
 */
public interface Module {
	/**
	 * This method will boot the module. It should check for other module
	 * dependencies.
	 */
	void boot();

	/**
	 * This method will run the initialisation script for the module.
	 */
	void initialise();
}
