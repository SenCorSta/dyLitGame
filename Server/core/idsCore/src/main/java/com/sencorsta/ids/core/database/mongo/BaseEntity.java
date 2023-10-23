package com.sencorsta.ids.core.database.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    @Getter
    private ObjectId id;

    private Date createdAt;

    private Date updatedAt;

    public void update() {
        if (id == null) {
            this.createdAt = new Date();
            this.updatedAt = new Date();
        } else {
            this.updatedAt = new Date();
        }

    }
}
