/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package de.jena.ogc_features.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gecko.emf.repository.EMFRepository;
import org.gecko.emf.repository.query.IQuery;
import org.gecko.emf.repository.query.QueryRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.LinkType;

import de.jena.ogc_features.service.api.CollectionsService;
import de.jena.ogc_features.service.api.FeatureIndexService;
import de.jena.ogc_features.service.api.FeatureSearchService;
import de.jena.ogc_features.service.impl.util.LinksUtil;
import net.opengis.ogcapi.features.conf.geojson.Feature;
import net.opengis.ogcapi.features.conf.geojson.FeatureCollection;
import net.opengis.ogcapi.features.conf.geojson.GeoJSONFactory;
import net.opengis.ogcapi.features.conf.geojson.GeoJSONPackage;
import net.opengis.ogcapi.features.core.BoundingBox;
import net.opengis.ogcapi.features.core.OGCAPIFeaturesCollection;
import net.opengis.ogcapi.features.core.OGCAPIFeaturesCollectionsWrapper;
import net.opengis.ogcapi.features.core.coreFactory;
import net.opengis.ogcapi.features.core.corePackage;

/**
 * Implementation of {@link de.jena.ogc_features.service.api.CollectionsService}
 * 
 * @author Michal H. Siemaszko
 */
@Component(name = "CollectionsService", scope = ServiceScope.PROTOTYPE)
public class CollectionsServiceImpl implements CollectionsService {
	private static final Logger LOG = LoggerFactory.getLogger(CollectionsServiceImpl.class);

	@Reference(target = "(repo_id=OGCAPIFeatures.OGCAPIFeatures)", scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private EMFRepository emfRepository;

	@Reference
	private FeatureIndexService featureIndexService;

	@Reference
	private FeatureSearchService featureSearchService;

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getCollections(java.lang.String, java.lang.String)
	 */
	@Override
	public OGCAPIFeaturesCollectionsWrapper getCollections(String baseUrl, String mediaType) {
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		QueryRepository queryRepo = (QueryRepository) emfRepository.getAdapter(QueryRepository.class);

		IQuery query = queryRepo.createQueryBuilder().allQuery().build();

		List<OGCAPIFeaturesCollection> collections = queryRepo
				.getEObjectsByQuery(corePackage.Literals.OGCAPI_FEATURES_COLLECTION, query);

		for (OGCAPIFeaturesCollection collection : collections) {
			createOGCAPIFeaturesCollectionLinks(collection, false, baseUrl, mediaType);
		}

		OGCAPIFeaturesCollectionsWrapper wrapper = coreFactory.eINSTANCE.createOGCAPIFeaturesCollectionsWrapper();
		wrapper.getCollections().addAll(collections);

		LinkType selfLink = LinksUtil.INSTANCE.createSelfLink(String.format("%s/collections", baseUrl), mediaType);
		wrapper.getLinks().add(selfLink);

		return wrapper;
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getCollection(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public OGCAPIFeaturesCollection getCollection(String collectionId, String baseUrl, String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		OGCAPIFeaturesCollection collection = emfRepository.getEObject(corePackage.Literals.OGCAPI_FEATURES_COLLECTION,
				collectionId);

		if (collection != null) {
			createOGCAPIFeaturesCollectionLinks(collection, true, baseUrl, mediaType);
		}

		return collection;
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getItems(java.lang.String, java.util.Optional, java.lang.String, java.lang.String)
	 */
	@Override
	public FeatureCollection getItems(String collectionId, Optional<BoundingBox> bboxOptional, String baseUrl,
			String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		if (bboxOptional.isPresent()) {
			return getItems(collectionId, bboxOptional.get(), CollectionsService.ITEMS_LIMIT_DEFAULT,
					CollectionsService.ITEMS_OFFSET_DEFAULT, baseUrl, mediaType);
		} else {
			return getItems(collectionId, CollectionsService.ITEMS_LIMIT_DEFAULT,
					CollectionsService.ITEMS_OFFSET_DEFAULT, baseUrl, mediaType);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getItems(java.lang.String, java.util.Optional, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public FeatureCollection getItems(String collectionId, Optional<BoundingBox> bboxOptional, long limit, long offset,
			String baseUrl, String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		if (bboxOptional.isPresent()) {
			return getItems(collectionId, bboxOptional.get(), limit, offset, baseUrl, mediaType);
		} else {
			return getItems(collectionId, limit, offset, baseUrl, mediaType);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getItems(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public FeatureCollection getItems(String collectionId, String bbox, String baseUrl, String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		return getItems(collectionId, maybeParseBboxParam(bbox), baseUrl, mediaType);
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getItems(java.lang.String, java.lang.String, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public FeatureCollection getItems(String collectionId, String bbox, long limit, long offset, String baseUrl,
			String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		return getItems(collectionId, maybeParseBboxParam(bbox), limit, offset, baseUrl, mediaType);
	}

	/* 
	 * (non-Javadoc)
	 * @see de.jena.ogc_features.service.api.CollectionsService#getItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Feature getItem(String collectionId, String featureId, String baseUrl, String mediaType) {
		Objects.requireNonNull(collectionId, "Collection Id is required!");
		Objects.requireNonNull(featureId, "Feature Id is required!");
		Objects.requireNonNull(baseUrl, "Base URL is required!");
		Objects.requireNonNull(mediaType, "Media type is required!");

		QueryRepository queryRepo = (QueryRepository) emfRepository.getAdapter(QueryRepository.class);

		// @formatter:off
		IQuery query = queryRepo.createQueryBuilder()
				.column(GeoJSONPackage.Literals.FEATURE_COLLECTION__ID)
				.simpleValue(collectionId).build();
		// @formatter:on

		FeatureCollection featureCollection = queryRepo.getEObjectByQuery(GeoJSONPackage.Literals.FEATURE_COLLECTION,
				query, null);

		Feature feature = null;

		if (featureCollection != null) {

			feature = featureCollection.getFeatures().stream().filter(f -> featureId.equals(f.getId())).findFirst()
					.orElse(null);

			queryRepo.detach(featureCollection);

			if (feature != null) {
				createFeatureLinks(feature, collectionId, featureId, baseUrl, mediaType);
			}
		}

		return feature;
	}

	private FeatureCollection getItems(String collectionId, BoundingBox bbox, long limit, long offset, String baseUrl,
			String mediaType) {
		FeatureCollection featureCollection = GeoJSONFactory.eINSTANCE.createFeatureCollection();
		featureCollection.setId(collectionId);

		List<Feature> features = featureSearchService.searchFeaturesByBoundingBox(collectionId, bbox);

		long totalCount = 0;

		if (features != null && !features.isEmpty()) {

			totalCount = features.size();

			// @formatter:off
			List<Feature> limitedFeatures = features.stream()
					.skip(offset)
					.limit(limit)
					.toList();
			// @formatter:on			

			featureCollection.getFeatures().addAll(limitedFeatures);
		}

		createFeatureCollectionLinks(featureCollection, collectionId, limit, offset, totalCount, baseUrl, mediaType);

		return featureCollection;
	}

	private FeatureCollection getItems(String collectionId, long limit, long offset, String baseUrl, String mediaType) {
		QueryRepository queryRepo = (QueryRepository) emfRepository.getAdapter(QueryRepository.class);

		// @formatter:off
		IQuery query = queryRepo.createQueryBuilder()
				.column(GeoJSONPackage.Literals.FEATURE_COLLECTION__ID)
				.simpleValue(collectionId)
				.build();
		// @formatter:on		

		FeatureCollection featureCollection = EcoreUtil
				.copy(queryRepo.getEObjectByQuery(GeoJSONPackage.Literals.FEATURE_COLLECTION, query, null));

		long totalCount = 0;

		if (!featureCollection.getFeatures().isEmpty()) {
			totalCount = featureCollection.getFeatures().size();

			// @formatter:off
			List<Feature> limitedFeatures = featureCollection.getFeatures().stream()
					.skip(offset)
					.limit(limit)
					.toList();
			// @formatter:on

			featureCollection.getFeatures().clear();
			featureCollection.getFeatures().addAll(limitedFeatures);
		}

		createFeatureCollectionLinks(featureCollection, collectionId, limit, offset, totalCount, baseUrl, mediaType);

		return featureCollection;
	}

	// input format: xMin,yMin,xMax,yMax
	private Optional<BoundingBox> maybeParseBboxParam(String bboxParam) {
		if (bboxParam == null || bboxParam.isBlank()) {
			return Optional.empty();
		}

		String[] bboxParamArr = bboxParam.split(",");
		if (bboxParamArr.length != 4) {
			return Optional.empty();
		}

		try {
			BoundingBox bbox = coreFactory.eINSTANCE.createBoundingBox();

			bbox.setLowerCorner(
					new Double[] { Double.parseDouble(bboxParamArr[0]), Double.parseDouble(bboxParamArr[1]) }); // xMin,
																												// yMin
			bbox.setUpperCorner(
					new Double[] { Double.parseDouble(bboxParamArr[2]), Double.parseDouble(bboxParamArr[3]) }); // xMax,
																												// yMax
			return Optional.of(bbox);
		} catch (Throwable t) {
			return Optional.empty();
		}
	}

	private void createOGCAPIFeaturesCollectionLinks(OGCAPIFeaturesCollection collection, boolean showSelfLink,
			String baseUrl, String mediaType) {
		if (showSelfLink) {
			LinkType selfLink = LinksUtil.INSTANCE
					.createSelfLink(String.format("%s/collections/%s", baseUrl, collection.getId()), mediaType);
			collection.getLinks().add(selfLink);
		}

		LinkType itemsLink = LinksUtil.INSTANCE.createLink(
				String.format("%s/collections/%s/items", baseUrl, collection.getId()), "items",
				String.format("Access the features in the collection '%s' as JSON", collection.getId()), mediaType);
		collection.getLinks().add(itemsLink);
	}

	private void createFeatureCollectionLinks(FeatureCollection featureCollection, String collectionId, long limit,
			long offset, long totalCount, String baseUrl, String mediaType) {
		LinkType selfLink = LinksUtil.INSTANCE
				.createSelfLink(String.format("%s/collections/%s/items", baseUrl, collectionId), mediaType);
		featureCollection.getLinks().add(selfLink);

		if (hasNextPage(limit, offset, totalCount)) {
			LinkType nextPageLink = LinksUtil.INSTANCE.createNextPageLink(
					String.format("%s/collections/%s/items?offset=%d", baseUrl, collectionId, (offset + limit)),
					mediaType);
			featureCollection.getLinks().add(nextPageLink);
		}

		if (offset > 0) {
			LinkType previousPageLink = LinksUtil.INSTANCE
					.createPreviousPageLink(String.format("%s/collections/%s/items?offset=%d", baseUrl, collectionId,
							calculatePreviousPageOffset(limit, offset)), mediaType);
			featureCollection.getLinks().add(previousPageLink);

			LinkType firstPageLink = LinksUtil.INSTANCE
					.createFirstPageLink(String.format("%s/collections/%s/items", baseUrl, collectionId), mediaType);
			featureCollection.getLinks().add(firstPageLink);
		}
	}

	private boolean hasNextPage(long limit, long offset, long totalCount) {
		if ((offset + limit) > totalCount) {
			return false;
		} else {
			return true;
		}
	}

	private long calculatePreviousPageOffset(long limit, long offset) {
		long previousPageOffset = (offset - limit);
		if (previousPageOffset > 0) {
			return previousPageOffset;
		} else {
			return 0;
		}
	}

	private void createFeatureLinks(Feature feature, String collectionId, String featureId, String baseUrl,
			String mediaType) {
		LinkType selfLink = LinksUtil.INSTANCE.createSelfLink(
				String.format("%s/collections/%s/items/%s", baseUrl, collectionId, featureId), mediaType);
		feature.getLinks().add(selfLink);

		LinkType itemsLink = LinksUtil.INSTANCE.createLink(
				String.format("%s/collections/%s/items", baseUrl, collectionId), "items",
				String.format("Access the features in the collection '%s' as JSON", collectionId), mediaType);
		feature.getLinks().add(itemsLink);
	}
}
