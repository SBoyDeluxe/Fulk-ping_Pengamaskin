package org.example.moneymachine.model;

/**
 * An Adaptor following the Adapter-design pattern({@linkplain <a href="https://en.wikipedia.org/wiki/Adapter_pattern">...</a>})
 * <br>
 *  Meaning :
 *          <ol>
 *              <li>It contains an instance of the dependence (I.E The adapatee)</li>
 *              <li>It can return the wished for adaptee as the Target-interface</li>
 *          </ol>
 * @param <D> - Dependent interface/class
 * @param <T> - Target interface/class
 */
public interface Adapter<D, T> {



    public T from(D dependentClass);
    public D to(T targetClass);



}
