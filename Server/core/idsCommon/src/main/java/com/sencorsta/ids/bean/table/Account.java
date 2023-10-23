package com.sencorsta.ids.bean.table;

import com.sencorsta.ids.core.database.mongo.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    String account;
    String password;
    String playerId;
}
