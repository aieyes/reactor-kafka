/*
 * Copyright (c) 2023 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.common.message;

import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.*;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.types.*;
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CreateTopicsRequestData implements ApiMessage {
    private CreatableTopicCollection topics;

    private int timeoutMs;

    private boolean validateOnly;

    public static final Schema SCHEMA_0 = new Schema(new Field("topics", new ArrayOf(CreatableTopic.SCHEMA_0), "The topics to create."), new Field("timeout_ms", Type.INT32, "How long to wait in milliseconds before timing out the request."));

    public static final Schema SCHEMA_1 = new Schema(new Field("topics", new ArrayOf(CreatableTopic.SCHEMA_0), "The topics to create."), new Field("timeout_ms", Type.INT32, "How long to wait in milliseconds before timing out the request."), new Field("validate_only", Type.BOOLEAN, "If true, check that the topics can be created as specified, but don't create anything."));

    public static final Schema SCHEMA_2 = SCHEMA_1;

    public static final Schema SCHEMA_3 = SCHEMA_2;

    public static final Schema[] SCHEMAS = new Schema[]{SCHEMA_0, SCHEMA_1, SCHEMA_2, SCHEMA_3};

    public CreateTopicsRequestData(Readable readable, short version) {
        this.topics = new CreatableTopicCollection(0);
        read(readable, version);
    }

    public CreateTopicsRequestData(Struct struct, short version) {
        this.topics = new CreatableTopicCollection(0);
        fromStruct(struct, version);
    }

    public CreateTopicsRequestData() {
        this.topics = new CreatableTopicCollection(0);
        this.timeoutMs = 60000;
        this.validateOnly = false;
    }

    public short apiKey() {
        return 19;
    }

    public short lowestSupportedVersion() {
        return 0;
    }

    public short highestSupportedVersion() {
        return 3;
    }

    public void read(Readable readable, short version) {
        int arrayLength = readable.readInt();
        if (arrayLength < 0) {
            this.topics = null;
        } else {
            this.topics.clear(arrayLength);
            for (int i = 0; i < arrayLength; i++)
                this.topics.add(new CreatableTopic(readable, version));
        }
        this.timeoutMs = readable.readInt();
        if (version >= 1) {
            this.validateOnly = (readable.readByte() != 0);
        } else {
            this.validateOnly = false;
        }
    }

    public void write(Writable writable, short version) {
        writable.writeInt(this.topics.size());
        for (CreatableTopic element : this.topics)
            element.write(writable, version);
        writable.writeInt(this.timeoutMs);
        if (version >= 1)
            writable.writeByte((byte) (this.validateOnly ? 1 : 0));
    }

    public void fromStruct(Struct struct, short version) {
        Object[] nestedObjects = struct.getArray("topics");
        this.topics = new CreatableTopicCollection(nestedObjects.length);
        for (Object nestedObject : nestedObjects)
            this.topics.add(new CreatableTopic((Struct) nestedObject, version));
        this.timeoutMs = struct.getInt("timeout_ms").intValue();
        if (version >= 1) {
            this.validateOnly = struct.getBoolean("validate_only").booleanValue();
        } else {
            this.validateOnly = false;
        }
    }

    public Struct toStruct(short version) {
        Struct struct = new Struct(SCHEMAS[version]);
        Struct[] nestedObjects = new Struct[this.topics.size()];
        int i = 0;
        for (CreatableTopic element : this.topics)
            nestedObjects[i++] = element.toStruct(version);
        struct.set("topics", nestedObjects);
        struct.set("timeout_ms", Integer.valueOf(this.timeoutMs));
        if (version >= 1)
            struct.set("validate_only", Boolean.valueOf(this.validateOnly));
        return struct;
    }

    public int size(short version) {
        int size = 0;
        size += 4;
        for (CreatableTopic element : this.topics)
            size += element.size(version);
        size += 4;
        if (version >= 1) {
            size++;
        } else if (this.validateOnly) {
            throw new UnsupportedVersionException("Attempted to write a non-default validateOnly at version " + version);
        }
        return size;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CreateTopicsRequestData))
            return false;
        CreateTopicsRequestData other = (CreateTopicsRequestData) obj;
        if (this.topics == null) {
            if (other.topics != null)
                return false;
        } else if (!this.topics.equals(other.topics)) {
            return false;
        }
        if (this.timeoutMs != other.timeoutMs)
            return false;
        if (this.validateOnly != other.validateOnly)
            return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + ((this.topics == null) ? 0 : this.topics.hashCode());
        hashCode = 31 * hashCode + this.timeoutMs;
        hashCode = 31 * hashCode + (this.validateOnly ? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        return "CreateTopicsRequestData(topics=" +
                MessageUtil.deepToString(this.topics.iterator()) + ", timeoutMs=" + this.timeoutMs + ", validateOnly=" + (this.validateOnly ? "true" : "false") + ")";
    }

    public CreatableTopicCollection topics() {
        return this.topics;
    }

    public int timeoutMs() {
        return this.timeoutMs;
    }

    public boolean validateOnly() {
        return this.validateOnly;
    }

    public CreateTopicsRequestData setTopics(CreatableTopicCollection v) {
        this.topics = v;
        return this;
    }

    public CreateTopicsRequestData setTimeoutMs(int v) {
        this.timeoutMs = v;
        return this;
    }

    public CreateTopicsRequestData setValidateOnly(boolean v) {
        this.validateOnly = v;
        return this;
    }

    public static class CreatableTopic implements Message, ImplicitLinkedHashCollection.Element {
        private String name;

        private int numPartitions;

        private short replicationFactor;

        private CreatableReplicaAssignmentCollection assignments;

        private CreateableTopicConfigCollection configs;

        private int next;

        private int prev;

        public static final Schema SCHEMA_0 = new Schema(new Field[]{new Field("name", Type.STRING, "The topic name."), new Field("num_partitions", Type.INT32, "The number of partitions to create in the topic, or -1 if we are specifying a manual partition assignment."), new Field("replication_factor", Type.INT16, "The number of replicas to create for each partition in the topic, or -1 if we are specifying a manual partition assignment."), new Field("assignments", (Type) new ArrayOf((Type) CreatableReplicaAssignment.SCHEMA_0), "The manual partition assignment, or the empty array if we are using automatic assignment."), new Field("configs", (Type) new ArrayOf((Type) CreateableTopicConfig.SCHEMA_0), "The custom topic configurations to set.")});

        public static final Schema SCHEMA_1 = SCHEMA_0;

        public static final Schema SCHEMA_2 = SCHEMA_1;

        public static final Schema SCHEMA_3 = SCHEMA_2;

        public static final Schema[] SCHEMAS = new Schema[]{SCHEMA_0, SCHEMA_1, SCHEMA_2, SCHEMA_3};

        public CreatableTopic(Readable readable, short version) {
            this.assignments = new CreatableReplicaAssignmentCollection(0);
            this.configs = new CreateableTopicConfigCollection(0);
            read(readable, version);
        }

        public CreatableTopic(Struct struct, short version) {
            this.assignments = new CreatableReplicaAssignmentCollection(0);
            this.configs = new CreateableTopicConfigCollection(0);
            fromStruct(struct, version);
        }

        public CreatableTopic() {
            this.name = "";
            this.numPartitions = 0;
            this.replicationFactor = 0;
            this.assignments = new CreatableReplicaAssignmentCollection(0);
            this.configs = new CreateableTopicConfigCollection(0);
        }

        public short lowestSupportedVersion() {
            return 0;
        }

        public short highestSupportedVersion() {
            return 3;
        }

        public void read(Readable readable, short version) {
            this.name = readable.readNullableString();
            this.numPartitions = readable.readInt();
            this.replicationFactor = readable.readShort();
            int arrayLength = readable.readInt();
            if (arrayLength < 0) {
                this.assignments = null;
            } else {
                this.assignments.clear(arrayLength);
                for (int i = 0; i < arrayLength; i++)
                    this.assignments.add(new CreatableReplicaAssignment(readable, version));
            }
            arrayLength = readable.readInt();
            if (arrayLength < 0) {
                this.configs = null;
            } else {
                this.configs.clear(arrayLength);
                for (int i = 0; i < arrayLength; i++)
                    this.configs.add(new CreateableTopicConfig(readable, version));
            }
        }

        public void write(Writable writable, short version) {
            writable.writeString(this.name);
            writable.writeInt(this.numPartitions);
            writable.writeShort(this.replicationFactor);
            writable.writeInt(this.assignments.size());
            for (CreatableReplicaAssignment element : this.assignments)
                element.write(writable, version);
            writable.writeInt(this.configs.size());
            for (CreateableTopicConfig element : this.configs)
                element.write(writable, version);
        }

        public void fromStruct(Struct struct, short version) {
            this.name = struct.getString("name");
            this.numPartitions = struct.getInt("num_partitions").intValue();
            this.replicationFactor = struct.getShort("replication_factor").shortValue();
            Object[] nestedObjects = struct.getArray("assignments");
            this.assignments = new CreatableReplicaAssignmentCollection(nestedObjects.length);
            for (Object nestedObject : nestedObjects)
                this.assignments.add(new CreatableReplicaAssignment((Struct) nestedObject, version));
            nestedObjects = struct.getArray("configs");
            this.configs = new CreateableTopicConfigCollection(nestedObjects.length);
            for (Object nestedObject : nestedObjects)
                this.configs.add(new CreateableTopicConfig((Struct) nestedObject, version));
        }

        public Struct toStruct(short version) {
            Struct struct = new Struct(SCHEMAS[version]);
            struct.set("name", this.name);
            struct.set("num_partitions", Integer.valueOf(this.numPartitions));
            struct.set("replication_factor", Short.valueOf(this.replicationFactor));
            Struct[] nestedObjects = new Struct[this.assignments.size()];
            int i = 0;
            for (CreatableReplicaAssignment element : this.assignments)
                nestedObjects[i++] = element.toStruct(version);
            struct.set("assignments", nestedObjects);
            nestedObjects = new Struct[this.configs.size()];
            i = 0;
            for (CreateableTopicConfig element : this.configs)
                nestedObjects[i++] = element.toStruct(version);
            struct.set("configs", nestedObjects);
            return struct;
        }

        public int size(short version) {
            int size = 0;
            size += 2;
            size += MessageUtil.serializedUtf8Length(this.name);
            size += 4;
            size += 2;
            size += 4;
            for (CreatableReplicaAssignment element : this.assignments)
                size += element.size(version);
            size += 4;
            for (CreateableTopicConfig element : this.configs)
                size += element.size(version);
            return size;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CreatableTopic))
                return false;
            CreatableTopic other = (CreatableTopic) obj;
            if (this.name == null) {
                if (other.name != null)
                    return false;
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + ((this.name == null) ? 0 : this.name.hashCode());
            return hashCode;
        }

        public String toString() {
            return "CreatableTopic(name='" + this.name + "', numPartitions=" + this.numPartitions + ", replicationFactor=" + this.replicationFactor + ", assignments=" +

                    MessageUtil.deepToString(this.assignments.iterator()) + ", configs=" +
                    MessageUtil.deepToString(this.configs.iterator()) + ")";
        }

        public String name() {
            return this.name;
        }

        public int numPartitions() {
            return this.numPartitions;
        }

        public short replicationFactor() {
            return this.replicationFactor;
        }

        public CreatableReplicaAssignmentCollection assignments() {
            return this.assignments;
        }

        public CreateableTopicConfigCollection configs() {
            return this.configs;
        }

        public int next() {
            return this.next;
        }

        public int prev() {
            return this.prev;
        }

        public CreatableTopic setName(String v) {
            this.name = v;
            return this;
        }

        public CreatableTopic setNumPartitions(int v) {
            this.numPartitions = v;
            return this;
        }

        public CreatableTopic setReplicationFactor(short v) {
            this.replicationFactor = v;
            return this;
        }

        public CreatableTopic setAssignments(CreatableReplicaAssignmentCollection v) {
            this.assignments = v;
            return this;
        }

        public CreatableTopic setConfigs(CreateableTopicConfigCollection v) {
            this.configs = v;
            return this;
        }

        public void setNext(int v) {
            this.next = v;
        }

        public void setPrev(int v) {
            this.prev = v;
        }
    }

    public static class CreatableReplicaAssignment implements Message, ImplicitLinkedHashCollection.Element {
        private int partitionIndex;

        private List<Integer> brokerIds;

        private int next;

        private int prev;

        public static final Schema SCHEMA_0 = new Schema(new Field[]{new Field("partition_index", Type.INT32, "The partition index."), new Field("broker_ids", (Type) new ArrayOf(Type.INT32), "The brokers to place the partition on.")});

        public static final Schema SCHEMA_1 = SCHEMA_0;

        public static final Schema SCHEMA_2 = SCHEMA_1;

        public static final Schema SCHEMA_3 = SCHEMA_2;

        public static final Schema[] SCHEMAS = new Schema[]{SCHEMA_0, SCHEMA_1, SCHEMA_2, SCHEMA_3};

        public CreatableReplicaAssignment(Readable readable, short version) {
            this.brokerIds = new ArrayList<>();
            read(readable, version);
        }

        public CreatableReplicaAssignment(Struct struct, short version) {
            this.brokerIds = new ArrayList<>();
            fromStruct(struct, version);
        }

        public CreatableReplicaAssignment() {
            this.partitionIndex = 0;
            this.brokerIds = new ArrayList<>();
        }

        public short lowestSupportedVersion() {
            return 0;
        }

        public short highestSupportedVersion() {
            return 3;
        }

        public void read(Readable readable, short version) {
            this.partitionIndex = readable.readInt();
            int arrayLength = readable.readInt();
            if (arrayLength < 0) {
                this.brokerIds = null;
            } else {
                this.brokerIds.clear();
                for (int i = 0; i < arrayLength; i++)
                    this.brokerIds.add(Integer.valueOf(readable.readInt()));
            }
        }

        public void write(Writable writable, short version) {
            writable.writeInt(this.partitionIndex);
            writable.writeInt(this.brokerIds.size());
            for (Integer element : this.brokerIds)
                writable.writeInt(element.intValue());
        }

        public void fromStruct(Struct struct, short version) {
            this.partitionIndex = struct.getInt("partition_index").intValue();
            Object[] nestedObjects = struct.getArray("broker_ids");
            this.brokerIds = new ArrayList<>(nestedObjects.length);
            for (Object nestedObject : nestedObjects)
                this.brokerIds.add((Integer) nestedObject);
        }

        public Struct toStruct(short version) {
            Struct struct = new Struct(SCHEMAS[version]);
            struct.set("partition_index", Integer.valueOf(this.partitionIndex));
            Integer[] nestedObjects = new Integer[this.brokerIds.size()];
            int i = 0;
            for (Integer element : this.brokerIds)
                nestedObjects[i++] = element;
            struct.set("broker_ids", nestedObjects);
            return struct;
        }

        public int size(short version) {
            int size = 0;
            size += 4;
            size += 4;
            size += this.brokerIds.size() * 4;
            return size;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CreatableReplicaAssignment))
                return false;
            CreatableReplicaAssignment other = (CreatableReplicaAssignment) obj;
            if (this.partitionIndex != other.partitionIndex)
                return false;
            return true;
        }

        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + this.partitionIndex;
            return hashCode;
        }

        public String toString() {
            return "CreatableReplicaAssignment(partitionIndex=" + this.partitionIndex + ", brokerIds=" +

                    MessageUtil.deepToString(this.brokerIds.iterator()) + ")";
        }

        public int partitionIndex() {
            return this.partitionIndex;
        }

        public List<Integer> brokerIds() {
            return this.brokerIds;
        }

        public int next() {
            return this.next;
        }

        public int prev() {
            return this.prev;
        }

        public CreatableReplicaAssignment setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }

        public CreatableReplicaAssignment setBrokerIds(List<Integer> v) {
            this.brokerIds = v;
            return this;
        }

        public void setNext(int v) {
            this.next = v;
        }

        public void setPrev(int v) {
            this.prev = v;
        }
    }

    public static class CreatableReplicaAssignmentCollection extends ImplicitLinkedHashMultiCollection<CreatableReplicaAssignment> {
        public CreatableReplicaAssignmentCollection() {
        }

        public CreatableReplicaAssignmentCollection(int expectedNumElements) {
            super(expectedNumElements);
        }

        public CreatableReplicaAssignmentCollection(Iterator<CreatableReplicaAssignment> iterator) {
            super(iterator);
        }

        public CreatableReplicaAssignment find(int partitionIndex) {
            CreatableReplicaAssignment key = new CreatableReplicaAssignment();
            key.setPartitionIndex(partitionIndex);
            return find(key);
        }

        public List<CreatableReplicaAssignment> findAll(int partitionIndex) {
            CreatableReplicaAssignment key = new CreatableReplicaAssignment();
            key.setPartitionIndex(partitionIndex);
            return findAll(key);
        }
    }

    public static class CreateableTopicConfig implements Message, ImplicitLinkedHashCollection.Element {
        private String name;

        private String value;

        private int next;

        private int prev;

        public static final Schema SCHEMA_0 = new Schema(new Field[]{new Field("name", Type.STRING, "The configuration name."), new Field("value", Type.NULLABLE_STRING, "The configuration value.")});

        public static final Schema SCHEMA_1 = SCHEMA_0;

        public static final Schema SCHEMA_2 = SCHEMA_1;

        public static final Schema SCHEMA_3 = SCHEMA_2;

        public static final Schema[] SCHEMAS = new Schema[]{SCHEMA_0, SCHEMA_1, SCHEMA_2, SCHEMA_3};

        public CreateableTopicConfig(Readable readable, short version) {
            read(readable, version);
        }

        public CreateableTopicConfig(Struct struct, short version) {
            fromStruct(struct, version);
        }

        public CreateableTopicConfig() {
            this.name = "";
            this.value = "";
        }

        public short lowestSupportedVersion() {
            return 0;
        }

        public short highestSupportedVersion() {
            return 3;
        }

        public void read(Readable readable, short version) {
            this.name = readable.readNullableString();
            this.value = readable.readNullableString();
        }

        public void write(Writable writable, short version) {
            writable.writeString(this.name);
            writable.writeNullableString(this.value);
        }

        public void fromStruct(Struct struct, short version) {
            this.name = struct.getString("name");
            this.value = struct.getString("value");
        }

        public Struct toStruct(short version) {
            Struct struct = new Struct(SCHEMAS[version]);
            struct.set("name", this.name);
            struct.set("value", this.value);
            return struct;
        }

        public int size(short version) {
            int size = 0;
            size += 2;
            size += MessageUtil.serializedUtf8Length(this.name);
            size += 2;
            if (this.value != null)
                size += MessageUtil.serializedUtf8Length(this.value);
            return size;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CreateableTopicConfig))
                return false;
            CreateableTopicConfig other = (CreateableTopicConfig) obj;
            if (this.name == null) {
                if (other.name != null)
                    return false;
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + ((this.name == null) ? 0 : this.name.hashCode());
            return hashCode;
        }

        public String toString() {
            return "CreateableTopicConfig(name='" + this.name + "', value='" + this.value + "')";
        }

        public String name() {
            return this.name;
        }

        public String value() {
            return this.value;
        }

        public int next() {
            return this.next;
        }

        public int prev() {
            return this.prev;
        }

        public CreateableTopicConfig setName(String v) {
            this.name = v;
            return this;
        }

        public CreateableTopicConfig setValue(String v) {
            this.value = v;
            return this;
        }

        public void setNext(int v) {
            this.next = v;
        }

        public void setPrev(int v) {
            this.prev = v;
        }
    }

    public static class CreateableTopicConfigCollection extends ImplicitLinkedHashMultiCollection<CreateableTopicConfig> {
        public CreateableTopicConfigCollection() {
        }

        public CreateableTopicConfigCollection(int expectedNumElements) {
            super(expectedNumElements);
        }

        public CreateableTopicConfigCollection(Iterator<CreateableTopicConfig> iterator) {
            super(iterator);
        }

        public CreateableTopicConfig find(String name) {
            CreateableTopicConfig key = new CreateableTopicConfig();
            key.setName(name);
            return find(key);
        }

        public List<CreateableTopicConfig> findAll(String name) {
            CreateableTopicConfig key = new CreateableTopicConfig();
            key.setName(name);
            return findAll(key);
        }
    }

    public static class CreatableTopicCollection extends ImplicitLinkedHashMultiCollection<CreatableTopic> {
        public CreatableTopicCollection() {
        }

        public CreatableTopicCollection(int expectedNumElements) {
            super(expectedNumElements);
        }

        public CreatableTopicCollection(Iterator<CreatableTopic> iterator) {
            super(iterator);
        }

        public CreatableTopic find(String name) {
            CreatableTopic key = new CreatableTopic();
            key.setName(name);
            return find(key);
        }

        public List<CreatableTopic> findAll(String name) {
            CreatableTopic key = new CreatableTopic();
            key.setName(name);
            return findAll(key);
        }
    }
}
