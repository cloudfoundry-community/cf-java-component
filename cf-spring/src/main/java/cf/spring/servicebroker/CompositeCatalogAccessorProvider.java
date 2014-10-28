/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.spring.servicebroker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Composite {@link CatalogAccessorProvider} merging {@link CatalogAccessor}
 * of other providers.
 *
 * @author Sebastien Gerard
 */
public class CompositeCatalogAccessorProvider implements CatalogAccessorProvider {

    private final Collection<CatalogAccessorProvider> providers;

    public CompositeCatalogAccessorProvider(Collection<CatalogAccessorProvider> providers) {
        this.providers = providers;
    }

    @Override
    public CatalogAccessor getCatalogAccessor() {
        final List<CatalogAccessor> catalogAccessors = new ArrayList<>();

        for (CatalogAccessorProvider provider : providers) {
            catalogAccessors.add(provider.getCatalogAccessor());
        }

        return new CatalogAccessor(catalogAccessors.toArray(new CatalogAccessor[catalogAccessors.size()]));
    }
}
