CREATE DATABASE  IF NOT EXISTS `teamproject_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `teamproject_db`;

--
-- Table structure for table `emailtemplate`
--

DROP TABLE IF EXISTS `emailtemplate`;

CREATE TABLE `emailtemplate` (
                                 `emailTemplateType` varchar(255) NOT NULL,
                                 `subject` varchar(255) NOT NULL,
                                 `template` longtext NOT NULL,
                                 PRIMARY KEY (`emailTemplateType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `usergroup`
--

DROP TABLE IF EXISTS `usergroup`;

CREATE TABLE `usergroup` (
                             `groupId` bigint(20) NOT NULL,
                             `groupName` varchar(255) NOT NULL,
                             PRIMARY KEY (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `userId` bigint(20) NOT NULL,
                        `tokenJwt` longtext,
                        `userEmail` varchar(50) DEFAULT NULL,
                        `userFirstName` varchar(50) DEFAULT NULL,
                        `userLastName` varchar(50) DEFAULT NULL,
                        `userName` varchar(25) NOT NULL,
                        `userPassword` varchar(100) NOT NULL,
                        `matrikelNumber` int(11) DEFAULT NULL,
                        `courseOfStudies` varchar(100) DEFAULT NULL,
                        `profilePicture` longblob,
                        `userGraduation` varchar(50) DEFAULT NULL,
                        `userSemesters` int(11) DEFAULT NULL,
                        `error` int(11) NOT NULL,
                        PRIMARY KEY (`userId`),
                        UNIQUE KEY `userName_UNIQUE` (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `user_usergroup`
--


DROP TABLE IF EXISTS `user_usergroup`;

CREATE TABLE `user_usergroup` (
                                  `groupId` bigint(20) NOT NULL,
                                  `userId` bigint(20) NOT NULL,
                                  KEY `FK_inoqtvqd8og359l829tm6g6kb` (`userId`),
                                  KEY `FK_2fkw33hh5n4laxr9s6mknirtb` (`groupId`),
                                  CONSTRAINT `FK_2fkw33hh5n4laxr9s6mknirtb` FOREIGN KEY (`groupId`) REFERENCES `usergroup` (`groupId`),
                                  CONSTRAINT `FK_inoqtvqd8og359l829tm6g6kb` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `usergroup_usergroup`
--

DROP TABLE IF EXISTS `usergroup_usergroup`;

CREATE TABLE `usergroup_usergroup` (
                                       `UserGroup_groupId` bigint(20) NOT NULL,
                                       `subgroups_groupId` bigint(20) NOT NULL,
                                       `parentGroups_groupId` bigint(20) NOT NULL,
                                       KEY `FK_k42v5yfq5ya0yh5rkcaub8jsv` (`subgroups_groupId`),
                                       KEY `FK_7rdrmc0utoljgnnl6rux0wac1` (`UserGroup_groupId`),
                                       KEY `FK_9phlyurr4gxpgrrn3xdj6ncux` (`parentGroups_groupId`),
                                       CONSTRAINT `FK_7rdrmc0utoljgnnl6rux0wac1` FOREIGN KEY (`UserGroup_groupId`) REFERENCES `usergroup` (`groupId`),
                                       CONSTRAINT `FK_9phlyurr4gxpgrrn3xdj6ncux` FOREIGN KEY (`parentGroups_groupId`) REFERENCES `usergroup` (`groupId`),
                                       CONSTRAINT `FK_k42v5yfq5ya0yh5rkcaub8jsv` FOREIGN KEY (`subgroups_groupId`) REFERENCES `usergroup` (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `systemvariable`
--

DROP TABLE IF EXISTS `systemvariable`;

CREATE TABLE `systemvariable` (
                                  `var_key` varchar(255) NOT NULL,
                                  `name` varchar(255) DEFAULT NULL,
                                  `required` bit(1) DEFAULT NULL,
                                  `value` varchar(255) DEFAULT NULL,
                                  PRIMARY KEY (`var_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `specialisation`
--

DROP TABLE IF EXISTS `specialisation`;

CREATE TABLE `specialisation` (
                                  `specialisationId` bigint(20) NOT NULL,
                                  `specialisationName` varchar(25) NOT NULL,
                                  PRIMARY KEY (`specialisationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `secret`
--

DROP TABLE IF EXISTS `secret`;

CREATE TABLE `secret` (
                          `secret_key` varchar(255) NOT NULL,
                          `isRequired` bit(1) DEFAULT NULL,
                          `tooltipMessage` varchar(255) DEFAULT NULL,
                          `value` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`secret_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `rightdetails`
--

DROP TABLE IF EXISTS `rightdetails`;

CREATE TABLE `rightdetails` (
                                `rightId` varchar(255) NOT NULL,
                                `description` varchar(255) NOT NULL,
                                PRIMARY KEY (`rightId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `roleabstract`
--

DROP TABLE IF EXISTS `roleabstract`;

CREATE TABLE `roleabstract` (
                                `type` varchar(31) NOT NULL,
                                `roleId` bigint(20) NOT NULL,
                                `created` datetime DEFAULT NULL,
                                `defaultRoleKey` varchar(255) DEFAULT NULL,
                                `roleDescription` longtext,
                                `roleName` longtext NOT NULL,
                                PRIMARY KEY (`roleId`),
                                UNIQUE KEY `UK_9t09kdbx5eujfw21cugg7o9ds` (`defaultRoleKey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `metaproject`
--

DROP TABLE IF EXISTS `metaproject`;

CREATE TABLE `metaproject` (
                               `metaProjectId` bigint(20) NOT NULL,
                               `courseOfStudies` varchar(25) DEFAULT NULL,
                               `description` longtext,
                               `metaProjectTitle` varchar(50) NOT NULL,
                               `projectRegEnd` varchar(255) NOT NULL,
                               `projectRegStart` varchar(255) NOT NULL,
                               `semester` int(11) DEFAULT NULL,
                               `studentRegEnd` varchar(255) NOT NULL,
                               `studentRegStart` varchar(255) NOT NULL,
                               `teamMaxSize` int(11) NOT NULL,
                               `teamMinSize` int(11) NOT NULL,
                               `teamRegEnd` varchar(255) NOT NULL,
                               `teamRegStart` varchar(255) NOT NULL,
                               `metaProjectLeader` bigint(20) DEFAULT NULL,
                               `deadline` varchar(255) NOT NULL,
                               `registerType` varchar(255) NOT NULL,
                               `public` tinyint(1) DEFAULT '1',
                               `preRegistration` tinyint(1) DEFAULT '1',
                               PRIMARY KEY (`metaProjectId`),
                               KEY `FK_kupqu53n66nc15fx00wrd6n9h` (`metaProjectLeader`),
                               CONSTRAINT `FKqc93itlcwglg51vr3c43h4wof` FOREIGN KEY (`metaProjectLeader`) REFERENCES `user` (`userId`),
                               CONSTRAINT `FK_kupqu53n66nc15fx00wrd6n9h` FOREIGN KEY (`metaProjectLeader`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
                           `projectId` bigint(20) NOT NULL,
                           `kickOffDate` varchar(50) NOT NULL,
                           `kickOffLocation` longtext,
                           `projectDescription` longtext,
                           `projectTitle` varchar(100) NOT NULL,
                           `sollProject` bit(1) DEFAULT NULL,
                           `metaProjectId` bigint(20) DEFAULT NULL,
                           `projectLeader` bigint(20) DEFAULT NULL,
                           `isAssigned` bit(1) NOT NULL,
                           `maxAmountMember` int(11) NOT NULL,
                           `minAmountMember` int(11) NOT NULL,
                           `shortProjectDescription` longtext,
                           `endDate` varchar(255) DEFAULT NULL,
                           `amountOfTeammembers` int(11) NOT NULL,
                           `lastUser` varchar(255) NOT NULL,
                           `projectStatus` varchar(255) NOT NULL,
                           `statusCode` int(11) NOT NULL,
                           `timeStamp` varchar(255) NOT NULL,
                           `transferUser` varchar(255) NOT NULL,
                           PRIMARY KEY (`projectId`),
                           KEY `FK_pi4unt105uwc49oot18udt7wl` (`metaProjectId`),
                           KEY `FK_2g1p287go0dalni0kiljbvc1l` (`projectLeader`),
                           CONSTRAINT `FKohbeppe0vofl4xfvtgxjwjfch` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                           CONSTRAINT `FKq9wcw0m8fuvu16xo9wma7r8ju` FOREIGN KEY (`projectLeader`) REFERENCES `user` (`userId`),
                           CONSTRAINT `FK_2g1p287go0dalni0kiljbvc1l` FOREIGN KEY (`projectLeader`) REFERENCES `user` (`userId`),
                           CONSTRAINT `FK_pi4unt105uwc49oot18udt7wl` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `categoryreminder`
--

DROP TABLE IF EXISTS `categoryreminder`;

CREATE TABLE `categoryreminder` (
                                    `reminderId` bigint(20) NOT NULL,
                                    `category` varchar(255) DEFAULT NULL,
                                    `text` varchar(255) DEFAULT NULL,
                                    /*`text` longtext DEFAULT NULL, ---------> so ists im Test */
                                    PRIMARY KEY (`reminderId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `fileupload`
--

DROP TABLE IF EXISTS `fileupload`;

CREATE TABLE `fileupload` (
                              `uploadId` bigint(20) NOT NULL,
                              `fileName` varchar(255) NOT NULL,
                              `filePath` varchar(255) NOT NULL,
                              `projectId` bigint(20) NOT NULL,
                              `type` int(11) NOT NULL,
                              PRIMARY KEY (`uploadId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;

CREATE TABLE `hibernate_sequence` (
    `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `project_fileupload`
--

DROP TABLE IF EXISTS `project_fileupload`;

CREATE TABLE `project_fileupload` (
                                      `Project_projectId` bigint(20) NOT NULL,
                                      `fileUpload_uploadId` bigint(20) NOT NULL,
                                      UNIQUE KEY `UK_aq058a9639cgr4tgsr7v3n1tw` (`fileUpload_uploadId`),
                                      KEY `FKfuv7ehctfbbl92y6q0chks25q` (`Project_projectId`),
                                      CONSTRAINT `FKahnennrdk848decx33yhv2l0i` FOREIGN KEY (`fileUpload_uploadId`) REFERENCES `fileupload` (`uploadId`),
                                      CONSTRAINT `FKfuv7ehctfbbl92y6q0chks25q` FOREIGN KEY (`Project_projectId`) REFERENCES `project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
                        `teamId` bigint(20) NOT NULL,
                        `projectAssignmentStatus` int(11) DEFAULT NULL,
                        `teamName` varchar(25) NOT NULL,
                        `updateStatus` int(11) NOT NULL,
                        `metaProjectId` bigint(20) DEFAULT NULL,
                        `projectId` bigint(20) DEFAULT NULL,
                        `teamLeader` bigint(20) DEFAULT NULL,
                        PRIMARY KEY (`teamId`),
                        KEY `FKlmbpho0pnixyg9bnx0nmhqxko` (`metaProjectId`),
                        KEY `FKssskkjmcxxpoke73e15kecr61` (`projectId`),
                        KEY `FK12yj9f016x79f5bit6783bkdh` (`teamLeader`),
                        CONSTRAINT `FK12yj9f016x79f5bit6783bkdh` FOREIGN KEY (`teamLeader`) REFERENCES `user` (`userId`),
                        CONSTRAINT `FKlmbpho0pnixyg9bnx0nmhqxko` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                        CONSTRAINT `FKssskkjmcxxpoke73e15kecr61` FOREIGN KEY (`projectId`) REFERENCES `project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `tokenkey`
--

DROP TABLE IF EXISTS `tokenkey`;

CREATE TABLE `tokenkey` (
                            `TokenKeyId` bigint(20) NOT NULL,
                            `KeyValue` longtext,
                            PRIMARY KEY (`TokenKeyId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `relationteamuser`
--

DROP TABLE IF EXISTS `relationteamuser`;

CREATE TABLE `relationteamuser` (
                                    `relationTeamUserId` bigint(20) NOT NULL,
                                    `inviteDate` varchar(255) DEFAULT NULL,
                                    `teamMemberStatus` int(11) NOT NULL,
                                    `teamId` bigint(20) NOT NULL,
                                    `userId` bigint(20) DEFAULT NULL,
                                    PRIMARY KEY (`relationTeamUserId`),
                                    UNIQUE KEY `UK26xc7x2dh26u3xx1v7d0vnce7` (`teamId`,`userId`),
                                    UNIQUE KEY `UK_26xc7x2dh26u3xx1v7d0vnce7` (`teamId`,`userId`),
                                    KEY `FK65vbaipv1vejuljc5n0gte0av` (`userId`),
                                    CONSTRAINT `FK65vbaipv1vejuljc5n0gte0av` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
                                    CONSTRAINT `FKqwvvt548tek9lhh0bvhfv5uf1` FOREIGN KEY (`teamId`) REFERENCES `team` (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `metaproject_projectcreatorsgroups`
--

DROP TABLE IF EXISTS `metaproject_projectcreatorsgroups`;

CREATE TABLE `metaproject_projectcreatorsgroups` (
                                                     `Metaproject_metaProjectId` bigint(20) NOT NULL,
                                                     `projectCreatorGroups_groupId` bigint(20) NOT NULL,
                                                     KEY `FK_q9ifqwmeso8qu4vi4f9cuw384` (`projectCreatorGroups_groupId`),
                                                     KEY `FK_11oin10ocf6n5db94u6tue73s` (`Metaproject_metaProjectId`),
                                                     CONSTRAINT `FK_11oin10ocf6n5db94u6tue73s` FOREIGN KEY (`Metaproject_metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                                                     CONSTRAINT `FK_q9ifqwmeso8qu4vi4f9cuw384` FOREIGN KEY (`projectCreatorGroups_groupId`) REFERENCES `usergroup` (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `prioteamproject`
--

DROP TABLE IF EXISTS `prioteamproject`;

CREATE TABLE `prioteamproject` (
                                   `prioTeamProject` bigint(20) NOT NULL,
                                   `prioritisation` varchar(25) NOT NULL,
                                   `status` int(11) NOT NULL,
                                   `projectId` bigint(20) DEFAULT NULL,
                                   `teamId` bigint(20) DEFAULT NULL,
                                   PRIMARY KEY (`prioTeamProject`),
                                   KEY `FKkpgjxgjagqge9w1d73f8gtdh6` (`projectId`),
                                   KEY `FK52hhptry6mmmuidlxyt9k7fhh` (`teamId`),
                                   CONSTRAINT `FK52hhptry6mmmuidlxyt9k7fhh` FOREIGN KEY (`teamId`) REFERENCES `team` (`teamId`),
                                   CONSTRAINT `FKkpgjxgjagqge9w1d73f8gtdh6` FOREIGN KEY (`projectId`) REFERENCES `project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `projectfieldaccess`
--

DROP TABLE IF EXISTS `projectfieldaccess`;

CREATE TABLE `projectfieldaccess` (
                                      `id` bigint(20) NOT NULL,
                                      `field` varchar(255) DEFAULT NULL,
                                      `projectId` bigint(20) DEFAULT NULL,
                                      `visible` bit(1) DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `relationmetaprojectuser`
--

DROP TABLE IF EXISTS `relationmetaprojectuser`;

CREATE TABLE `relationmetaprojectuser` (
                                           `relationMetaProjectUserId` bigint(20) NOT NULL,
                                           `metaProjectId` bigint(20) DEFAULT NULL,
                                           `userId` bigint(20) DEFAULT NULL,
                                           PRIMARY KEY (`relationMetaProjectUserId`),
                                           KEY `FKs8q0exog9apnbwly9icy7663u` (`metaProjectId`),
                                           KEY `FKn5243qkw8j8dtkc4sr1i88if4` (`userId`),
                                           CONSTRAINT `FKn5243qkw8j8dtkc4sr1i88if4` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
                                           CONSTRAINT `FKs8q0exog9apnbwly9icy7663u` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                                           CONSTRAINT `FK_meta_user_rel` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
                                           CONSTRAINT `FK_meta_user_rel_meta` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `automaticreminder`
--

DROP TABLE IF EXISTS `automaticreminder`;

CREATE TABLE `automaticreminder` (
                                     `autoRemId` bigint(20) NOT NULL,
                                     `firstDate` varchar(255) NOT NULL,
                                     `secDate` varchar(255) NOT NULL,
                                     `text` longtext,
                                     `metaProjectId` bigint(20) NOT NULL,
                                     `reminderId` bigint(20) NOT NULL,
                                     PRIMARY KEY (`autoRemId`),
                                     KEY `FK_ie16sfpygpud0pup63b34eryf` (`metaProjectId`),
                                     KEY `FK_8pnfc4kfs8b8us5caeu6menx3` (`reminderId`),
                                     CONSTRAINT `FK_8pnfc4kfs8b8us5caeu6menx3` FOREIGN KEY (`reminderId`) REFERENCES `categoryreminder` (`reminderId`),
                                     CONSTRAINT `FK_ie16sfpygpud0pup63b34eryf` FOREIGN KEY (`metaProjectId`) REFERENCES `metaproject` (`metaProjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `communication`
--

DROP TABLE IF EXISTS `communication`;

CREATE TABLE `communication` (
                                 `type` varchar(31) NOT NULL,
                                 `commId` bigint(20) NOT NULL,
                                 `created` datetime NOT NULL,
                                 `status` int(11) NOT NULL,
                                 `answer` int(11) DEFAULT NULL,
                                 `readStatus` int(11) DEFAULT NULL,
                                 `sendingUser_userId` bigint(20) DEFAULT NULL,
                                 `targetUser_userId` bigint(20) NOT NULL,
                                 `metaproject_metaProjectId` bigint(20) DEFAULT NULL,
                                 `newLeader_userId` bigint(20) DEFAULT NULL,
                                 `team_teamId` bigint(20) DEFAULT NULL,
                                 `oldLeader_userId` bigint(20) DEFAULT NULL,
                                 `templateType` varchar(255) NOT NULL,
                                 `metaprojectTitle` varchar(255) DEFAULT NULL,
                                 `teamName` varchar(255) DEFAULT NULL,
                                 `projectTitle` varchar(255) DEFAULT NULL,
                                 `receivingMember_userId` bigint(20) DEFAULT NULL,
                                 `project_projectId` bigint(20) DEFAULT NULL,
                                 `invitedMember_userId` bigint(20) DEFAULT NULL,
                                 `newMember_userId` bigint(20) DEFAULT NULL,
                                 `deletedUser_userId` bigint(20) DEFAULT NULL,
                                 PRIMARY KEY (`commId`),
                                 KEY `FKt331obovtfx4llldjb3nfotgf` (`sendingUser_userId`),
                                 KEY `FKsr3vab0v5dgdodd2qqa5erc4h` (`targetUser_userId`),
                                 KEY `FKn6c48xh4weol3va7hhwxjidp1` (`metaproject_metaProjectId`),
                                 KEY `FK434fmt5gahwnegjd1ra02rfhv` (`newLeader_userId`),
                                 KEY `FK3tn108je592ohpxv5nc30pbbt` (`team_teamId`),
                                 KEY `FKjp7fvb8claimkav5ttwhn5b8d` (`oldLeader_userId`),
                                 KEY `FKdy51qagod9j3yss9t46vol7to` (`receivingMember_userId`),
                                 KEY `FKgx7bwxpbccnoegjur3c4q690y` (`project_projectId`),
                                 KEY `FKiu7atevrfjjqvjtnc5by1hsm3` (`invitedMember_userId`),
                                 KEY `FK5kpnx9rr41nytklf2uhm5t6ot` (`newMember_userId`),
                                 KEY `FKc8qwcij0avbt14ovjp7c9da15` (`deletedUser_userId`),
                                 CONSTRAINT `FK3tn108je592ohpxv5nc30pbbt` FOREIGN KEY (`team_teamId`) REFERENCES `team` (`teamId`),
                                 CONSTRAINT `FK434fmt5gahwnegjd1ra02rfhv` FOREIGN KEY (`newLeader_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FK5kpnx9rr41nytklf2uhm5t6ot` FOREIGN KEY (`newMember_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKc8qwcij0avbt14ovjp7c9da15` FOREIGN KEY (`deletedUser_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKdy51qagod9j3yss9t46vol7to` FOREIGN KEY (`receivingMember_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKgx7bwxpbccnoegjur3c4q690y` FOREIGN KEY (`project_projectId`) REFERENCES `project` (`projectId`),
                                 CONSTRAINT `FKiu7atevrfjjqvjtnc5by1hsm3` FOREIGN KEY (`invitedMember_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKjp7fvb8claimkav5ttwhn5b8d` FOREIGN KEY (`oldLeader_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKn6c48xh4weol3va7hhwxjidp1` FOREIGN KEY (`metaproject_metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                                 CONSTRAINT `FKsr3vab0v5dgdodd2qqa5erc4h` FOREIGN KEY (`targetUser_userId`) REFERENCES `user` (`userId`),
                                 CONSTRAINT `FKt331obovtfx4llldjb3nfotgf` FOREIGN KEY (`sendingUser_userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `email`
--

DROP TABLE IF EXISTS `email`;

CREATE TABLE `email` (
                         `id` bigint(20) NOT NULL,
                         `body` longtext NOT NULL,
                         `status` varchar(255) NOT NULL,
                         `subject` varchar(255) NOT NULL,
                         `receiver_userId` bigint(20) NOT NULL,
                         `Communication_commId` bigint(20) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FKfbxd18yw6ad3bomj9p8y0jbpw` (`receiver_userId`),
                         KEY `FK_dwxln1gu5309q22k3152t3kch` (`Communication_commId`),
                         CONSTRAINT `FKfbxd18yw6ad3bomj9p8y0jbpw` FOREIGN KEY (`receiver_userId`) REFERENCES `user` (`userId`),
                         CONSTRAINT `FK_dwxln1gu5309q22k3152t3kch` FOREIGN KEY (`Communication_commId`) REFERENCES `communication` (`commId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `communication_email`
--

DROP TABLE IF EXISTS `communication_email`;

CREATE TABLE `communication_email` (
                                       `Communication_commId` bigint(20) NOT NULL,
                                       `email_id` bigint(20) NOT NULL,
                                       UNIQUE KEY `UK_i00ku0646w3vgg5wikeed0kr0` (`email_id`),
                                       KEY `FK7tbcbfe6euq4q9v71s77k0mxb` (`Communication_commId`),
                                       CONSTRAINT `FK7tbcbfe6euq4q9v71s77k0mxb` FOREIGN KEY (`Communication_commId`) REFERENCES `communication` (`commId`),
                                       CONSTRAINT `FKns7moju7y4s6fmbva4yu2qplv` FOREIGN KEY (`email_id`) REFERENCES `email` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `projectstatuschange`
--

DROP TABLE IF EXISTS `projectstatuschange`;

CREATE TABLE `projectstatuschange` (
                                       `statusChangeId` bigint(20) NOT NULL,
                                       `lastStatus` varchar(255) NOT NULL,
                                       `timeStamp` varchar(255) NOT NULL,
                                       `userName` varchar(255) NOT NULL,
                                       `project` bigint(20) DEFAULT NULL,
                                       PRIMARY KEY (`statusChangeId`),
                                       KEY `FK_2pak9m3xfcgsayox58f8se7b` (`project`),
                                       CONSTRAINT `FK_2pak9m3xfcgsayox58f8se7b` FOREIGN KEY (`project`) REFERENCES `project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `metaproject_specialisation`
--

DROP TABLE IF EXISTS `metaproject_specialisation`;

CREATE TABLE `metaproject_specialisation` (
                                              `Metaproject_metaProjectId` bigint(20) NOT NULL,
                                              `specialisation_specialisationId` bigint(20) NOT NULL,
                                              UNIQUE KEY `UK_mu3oo45i4p1ryr0jnbyx0woj6` (`specialisation_specialisationId`),
                                              KEY `FKo124hee8a80e5e9pcct9u3wyj` (`Metaproject_metaProjectId`),
                                              CONSTRAINT `FKkhlkmh8ehtnmh4gfed5tncl3q` FOREIGN KEY (`specialisation_specialisationId`) REFERENCES `specialisation` (`specialisationId`),
                                              CONSTRAINT `FKo124hee8a80e5e9pcct9u3wyj` FOREIGN KEY (`Metaproject_metaProjectId`) REFERENCES `metaproject` (`metaProjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `specialisationproject`
--

DROP TABLE IF EXISTS `specialisationproject`;

CREATE TABLE `specialisationproject` (
                                         `specialisationId` bigint(20) NOT NULL,
                                         `specialisationProportion` int(11) DEFAULT NULL,
                                         `specialisation_specialisationId` bigint(20) NOT NULL,
                                         PRIMARY KEY (`specialisationId`),
                                         KEY `FK1a4i6wh0blrgowsm9cjpmfi47` (`specialisation_specialisationId`),
                                         CONSTRAINT `FK1a4i6wh0blrgowsm9cjpmfi47` FOREIGN KEY (`specialisation_specialisationId`) REFERENCES `specialisation` (`specialisationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `project_specialisationproject`
--

DROP TABLE IF EXISTS `project_specialisationproject`;

CREATE TABLE `project_specialisationproject` (
                                                 `Project_projectId` bigint(20) NOT NULL,
                                                 `specialisation_specialisationId` bigint(20) NOT NULL,
                                                 UNIQUE KEY `UK_rsriqsty2bappevpplpc9lbc2` (`specialisation_specialisationId`),
                                                 KEY `FKif1fy5mb646f8cjvneksya1dv` (`Project_projectId`),
                                                 CONSTRAINT `FKfjyatb4ctb31a6ej9gnenglvy` FOREIGN KEY (`specialisation_specialisationId`) REFERENCES `specialisationproject` (`specialisationId`),
                                                 CONSTRAINT `FKif1fy5mb646f8cjvneksya1dv` FOREIGN KEY (`Project_projectId`) REFERENCES `project` (`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `metaproject_projectcreatorusers`
--

DROP TABLE IF EXISTS `metaproject_projectcreatorusers`;

CREATE TABLE `metaproject_projectcreatorusers` (
                                                   `Metaproject_metaProjectId` bigint(20) NOT NULL,
                                                   `projectCreatorUsers_userId` bigint(20) NOT NULL,
                                                   KEY `FK_ho4rr00dh9kkwksija5rejpnp` (`projectCreatorUsers_userId`),
                                                   KEY `FK_rnx37g3ifs2ctjpcf22x5v5s6` (`Metaproject_metaProjectId`),
                                                   CONSTRAINT `FK_ho4rr00dh9kkwksija5rejpnp` FOREIGN KEY (`projectCreatorUsers_userId`) REFERENCES `user` (`userId`),
                                                   CONSTRAINT `FK_rnx37g3ifs2ctjpcf22x5v5s6` FOREIGN KEY (`Metaproject_metaProjectId`) REFERENCES `metaproject` (`metaProjectId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `relationuserroleabstract`
--

DROP TABLE IF EXISTS `relationuserroleabstract`;

CREATE TABLE `relationuserroleabstract` (
                                            `type` varchar(31) NOT NULL,
                                            `id` bigint(20) NOT NULL,
                                            `user_userId` bigint(20) NOT NULL,
                                            `connected_metaProjectId` bigint(20) DEFAULT NULL,
                                            `role_roleId` bigint(20) DEFAULT NULL,
                                            `connected_projectId` bigint(20) DEFAULT NULL,
                                            `connected_userId` bigint(20) DEFAULT NULL,
                                            `connected_teamId` bigint(20) DEFAULT NULL,
                                            PRIMARY KEY (`id`),
                                            KEY `FK938apb4h4kmiak6pklhq0pwjb` (`user_userId`),
                                            KEY `FK6ce62isaf7dmelf70udb92ays` (`connected_metaProjectId`),
                                            KEY `FK8mgshlus36ed2h6f0oj7tvnhe` (`role_roleId`),
                                            KEY `FKi1l5sofynovo9njmj2hm252a5` (`connected_projectId`),
                                            KEY `FKdh9wu82lggos9ryk4ji8hctmw` (`connected_userId`),
                                            KEY `FKtn5g1xtgvabj5qsl6mujovqpa` (`connected_teamId`),
                                            CONSTRAINT `FK6ce62isaf7dmelf70udb92ays` FOREIGN KEY (`connected_metaProjectId`) REFERENCES `metaproject` (`metaProjectId`),
                                            CONSTRAINT `FK8mgshlus36ed2h6f0oj7tvnhe` FOREIGN KEY (`role_roleId`) REFERENCES `roleabstract` (`roleId`),
                                            CONSTRAINT `FK938apb4h4kmiak6pklhq0pwjb` FOREIGN KEY (`user_userId`) REFERENCES `user` (`userId`),
                                            CONSTRAINT `FKdh9wu82lggos9ryk4ji8hctmw` FOREIGN KEY (`connected_userId`) REFERENCES `user` (`userId`),
                                            CONSTRAINT `FKi1l5sofynovo9njmj2hm252a5` FOREIGN KEY (`connected_projectId`) REFERENCES `project` (`projectId`),
                                            CONSTRAINT `FKtn5g1xtgvabj5qsl6mujovqpa` FOREIGN KEY (`connected_teamId`) REFERENCES `team` (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `roleabstract_rolerights`
--

DROP TABLE IF EXISTS `roleabstract_rolerights`;

CREATE TABLE `roleabstract_rolerights` (
                                           `RoleAbstract_roleId` bigint(20) NOT NULL,
                                           `roleRights` varchar(255) DEFAULT NULL,
                                           KEY `FKr73cli42o8xlqujj1w8l7ox17` (`RoleAbstract_roleId`),
                                           CONSTRAINT `FKr73cli42o8xlqujj1w8l7ox17` FOREIGN KEY (`RoleAbstract_roleId`) REFERENCES `roleabstract` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



