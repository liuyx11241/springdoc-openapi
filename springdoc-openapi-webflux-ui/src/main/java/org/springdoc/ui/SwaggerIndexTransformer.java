/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springdoc.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.SwaggerUiOAuthProperties;
import reactor.core.publisher.Mono;

import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.resource.ResourceTransformer;
import org.springframework.web.reactive.resource.ResourceTransformerChain;
import org.springframework.web.reactive.resource.TransformedResource;
import org.springframework.web.server.ServerWebExchange;

public class SwaggerIndexTransformer extends  AbstractSwaggerIndexTransformer implements ResourceTransformer {

	public SwaggerIndexTransformer(SwaggerUiOAuthProperties swaggerUiOAuthProperties, ObjectMapper objectMapper) {
		super(swaggerUiOAuthProperties, objectMapper);
	}

	@Override
	public Mono<Resource> transform(ServerWebExchange serverWebExchange, Resource resource, ResourceTransformerChain resourceTransformerChain) {
		final AntPathMatcher antPathMatcher = new AntPathMatcher();
		boolean isIndexFound = false;
		try {
			isIndexFound = antPathMatcher.match("**/swagger-ui/**/index.html", resource.getURL().toString());
			if (isIndexFound && !CollectionUtils.isEmpty(swaggerUiOAuthProperties.getConfigParameters())) {
				String html = readFullyAsString(resource.getInputStream());
				html = addInitOauth(html);
				return Mono.just(new TransformedResource(resource, html.getBytes()));
			}
			else {
				return Mono.just(resource);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to transform Index", e);
		}
	}


}