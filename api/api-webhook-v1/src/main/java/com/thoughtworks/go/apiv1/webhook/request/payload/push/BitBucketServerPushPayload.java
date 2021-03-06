/*
 * Copyright 2020 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.apiv1.webhook.request.payload.push;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.apiv1.webhook.request.json.BitBucketServerRepository;
import com.thoughtworks.go.apiv1.webhook.request.json.BitBucketServerRepository.Link;
import com.thoughtworks.go.config.exceptions.BadRequestException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class BitBucketServerPushPayload implements PushPayload {
    @SerializedName("changes")
    private List<Change> changes;

    @SerializedName("repository")
    private BitBucketServerRepository repository;

    public BitBucketServerPushPayload() {
    }

    @Override
    public String getBranch() {
        return this.changes.stream()
                .filter(change -> change.ref != null)
                .filter(change -> equalsIgnoreCase(change.ref.type, "branch"))
                .map(change -> change.ref.displayId)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Payload must contain branch name!!"));
    }

    @Override
    public String getHostname() {
        return repository.getHostname();
    }

    @Override
    public String getFullName() {
        return repository.getFullName();
    }

    public String getScmType() {
        return repository.scm();
    }

    public List<String> getCloneUrls() {
        return repository.links().cloneLinks().stream()
                .map(withoutCredentials())
                .collect(Collectors.toList());
    }

    private Function<Link, String> withoutCredentials() {
        return link -> {
            try {
                URI actual = new URI(link.href());
                return new URI(actual.getScheme(),
                        null,
                        actual.getHost(),
                        actual.getPort(),
                        actual.getPath(),
                        actual.getQuery(),
                        actual.getFragment()).toString();
            } catch (URISyntaxException e) {
                return link.href();
            }
        };
    }

    public static class Change {
        @SerializedName("ref")
        private Ref ref;
    }

    public static class Ref {
        @SerializedName("displayId")
        private String displayId;

        @SerializedName("type")
        private String type;
    }
}
