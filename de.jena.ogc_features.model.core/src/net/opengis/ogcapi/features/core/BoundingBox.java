/*
 */
package net.opengis.ogcapi.features.core;

import java.math.BigInteger;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bounding Box</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link net.opengis.ogcapi.features.core.BoundingBox#getLowerCorner <em>Lower Corner</em>}</li>
 *   <li>{@link net.opengis.ogcapi.features.core.BoundingBox#getUpperCorner <em>Upper Corner</em>}</li>
 *   <li>{@link net.opengis.ogcapi.features.core.BoundingBox#getCrs <em>Crs</em>}</li>
 *   <li>{@link net.opengis.ogcapi.features.core.BoundingBox#getDimensions <em>Dimensions</em>}</li>
 * </ul>
 *
 * @see net.opengis.ogcapi.features.core.corePackage#getBoundingBox()
 * @model
 * @generated
 */
@ProviderType
public interface BoundingBox extends EObject {
	/**
	 * Returns the value of the '<em><b>Lower Corner</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Double}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lower Corner</em>' attribute list.
	 * @see net.opengis.ogcapi.features.core.corePackage#getBoundingBox_LowerCorner()
	 * @model lower="2" upper="2"
	 * @generated
	 */
	EList<Double> getLowerCorner();

	/**
	 * Returns the value of the '<em><b>Upper Corner</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Double}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Upper Corner</em>' attribute list.
	 * @see net.opengis.ogcapi.features.core.corePackage#getBoundingBox_UpperCorner()
	 * @model lower="2" upper="2"
	 * @generated
	 */
	EList<Double> getUpperCorner();

	/**
	 * Returns the value of the '<em><b>Crs</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Crs</em>' attribute.
	 * @see #setCrs(String)
	 * @see net.opengis.ogcapi.features.core.corePackage#getBoundingBox_Crs()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
	 * @generated
	 */
	String getCrs();

	/**
	 * Sets the value of the '{@link net.opengis.ogcapi.features.core.BoundingBox#getCrs <em>Crs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Crs</em>' attribute.
	 * @see #getCrs()
	 * @generated
	 */
	void setCrs(String value);

	/**
	 * Returns the value of the '<em><b>Dimensions</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dimensions</em>' attribute.
	 * @see #setDimensions(BigInteger)
	 * @see net.opengis.ogcapi.features.core.corePackage#getBoundingBox_Dimensions()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.PositiveInteger"
	 * @generated
	 */
	BigInteger getDimensions();

	/**
	 * Sets the value of the '{@link net.opengis.ogcapi.features.core.BoundingBox#getDimensions <em>Dimensions</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dimensions</em>' attribute.
	 * @see #getDimensions()
	 * @generated
	 */
	void setDimensions(BigInteger value);

} // BoundingBox
