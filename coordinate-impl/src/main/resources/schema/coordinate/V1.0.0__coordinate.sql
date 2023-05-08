
/*
 * coordinate 0.0.0 - 1.0.0
 */ 

CREATE TABLE IF NOT EXISTS `COO_AUTH_TOKEN` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account` varchar(128) NOT NULL,
  `aliasUid` varchar(128) NOT NULL,
  `clientIp` varchar(64) DEFAULT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `displayName` varchar(128) DEFAULT NULL,
  `domain` varchar(128) NOT NULL,
  `timeoutAt` bigint(20) NOT NULL,
  `token` varchar(512) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_DOMAIN` (
  `code` varchar(128) NOT NULL,
  `configYaml` longtext DEFAULT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `disabled` bit(1) NOT NULL,
  `name` varchar(128) NOT NULL,
  `provider` varchar(128) NOT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_INFO_CACHE` (
  `token` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `info` longtext DEFAULT NULL,
  `timeout` bigint(20) NOT NULL,
  PRIMARY KEY (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_JOB` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDateTime` bigint(20) NOT NULL,
  `error` bit(1) DEFAULT NULL,
  `finishedDateTime` bigint(20) DEFAULT NULL,
  `message` longtext DEFAULT NULL,
  `node` varchar(128) DEFAULT NULL,
  `queryUid` varchar(128) DEFAULT NULL,
  `requestUid` varchar(128) DEFAULT NULL,
  `resultJson` longtext DEFAULT NULL,
  `startedDateTime` bigint(20) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `subject` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext DEFAULT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `objType` varchar(256) DEFAULT NULL,
  `objUid` varchar(128) DEFAULT NULL,
  `reporter` varchar(256) DEFAULT NULL,
  `requestUid` varchar(128) DEFAULT NULL,
  `subjectType` varchar(256) DEFAULT NULL,
  `subjectUid` varchar(128) DEFAULT NULL,
  `userAccount` varchar(128) DEFAULT NULL,
  `userDomain` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_ORG` (
  `uid` varchar(128) NOT NULL,
  `code` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_CODE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_ORG_REC` (
  `uid` varchar(128) NOT NULL,
  `code` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `deletedDateTime` bigint(20) DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_ORG_USER_REL` (
  `organizationUid` varchar(128) NOT NULL,
  `userUid` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `type` varchar(128) NOT NULL,
  PRIMARY KEY (`organizationUid`,`userUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_PERMISSION` (
  `uid` varchar(128) NOT NULL,
  `action` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `principal` varchar(128) NOT NULL,
  `remark` varchar(128) DEFAULT NULL,
  `target` varchar(128) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_PRITARACT` (`principal`,`target`,`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_PROPERTY` (
  `name` varchar(128) NOT NULL,
  `objUid` varchar(128) NOT NULL,
  `category` varchar(128) DEFAULT NULL,
  `value` longtext DEFAULT NULL,
  PRIMARY KEY (`name`,`objUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_ROLE` (
  `uid` varchar(128) NOT NULL,
  `code` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_CODE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_ROLE_USER_REL` (
  `roleUid` varchar(128) NOT NULL,
  `userUid` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  PRIMARY KEY (`roleUid`,`userUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_USER` (
  `uid` varchar(128) NOT NULL,
  `account` varchar(128) NOT NULL,
  `aliasUid` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `disabled` bit(1) NOT NULL,
  `displayName` varchar(128) NOT NULL,
  `domain` varchar(128) NOT NULL,
  `domainUserIdentity` varchar(256) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `password` varchar(256) NOT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_ACTDOM` (`account`,`domain`),
  UNIQUE KEY `UK_ALIASUID` (`aliasUid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_USER_REC` (
  `uid` varchar(128) NOT NULL,
  `account` varchar(128) NOT NULL,
  `aliasUid` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `deletedDateTime` bigint(20) DEFAULT NULL,
  `displayName` varchar(128) NOT NULL,
  `domain` varchar(128) NOT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_HOOK` (
  `uid` varchar(128) NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `data` longtext DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `ownerType` varchar(128) DEFAULT NULL,
  `ownerUid` varchar(128) DEFAULT NULL,
  `subjectType` varchar(128) DEFAULT NULL,
  `subjectUid` varchar(128) DEFAULT NULL,
  `trigger` int(11) DEFAULT NULL,
  `triggerLife` int(11) DEFAULT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `zone` varchar(128) NOT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `COO_SECRET` (
  `uid` varchar(128) NOT NULL,
  `code` varchar(128) NOT NULL,
  `fingerprint` varchar(256) NOT NULL,
  `encryptedContent` longtext NOT NULL,
  `createdDateTime` bigint(20) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `updatedDateTime` bigint(20) DEFAULT NULL,
  `_ver` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_CODE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


ALTER TABLE COO_ORG_USER_REL ADD CONSTRAINT `FKCO_ORGUSERREL2ORG` FOREIGN KEY IF NOT EXISTS (`organizationUid`) REFERENCES `COO_ORG` (`uid`);
ALTER TABLE COO_ORG_USER_REL ADD CONSTRAINT `FKCO_ORGUSERREL2USER` FOREIGN KEY IF NOT EXISTS (`userUid`) REFERENCES `COO_USER` (`uid`);

ALTER TABLE COO_ROLE_USER_REL ADD CONSTRAINT `FKCO_ROLEUSERREL2ROLE` FOREIGN KEY IF NOT EXISTS (`roleUid`) REFERENCES `COO_ROLE` (`uid`);
ALTER TABLE COO_ROLE_USER_REL ADD CONSTRAINT `FKCO_ROLEUSERREL2USER` FOREIGN KEY IF NOT EXISTS (`userUid`) REFERENCES `COO_USER` (`uid`);
