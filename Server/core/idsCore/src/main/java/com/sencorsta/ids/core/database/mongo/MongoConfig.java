package com.sencorsta.ids.core.database.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mongo db 配置
 * @author daibin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MongoConfig {
	/**
	 * 链接地址
	 */
	String url;
	/**
	 * 数据库名
	 */
	String dataBaseName;
}
