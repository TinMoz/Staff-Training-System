-- 創建資料庫並設定字符集
DROP DATABASE IF EXISTS staff_training_system_database;
CREATE DATABASE staff_training_system_database 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_0900_ai_ci;

USE staff_training_system_database;

-- 創建 users 表
CREATE TABLE users (
  id int NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(100) NOT NULL,
  email varchar(100) NOT NULL,
  role enum('USER','ADMIN') NOT NULL DEFAULT 'USER',
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY username (username),
  UNIQUE KEY email (email),
  KEY idx_username (username)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- 插入 users 數據
INSERT INTO users (id, username, password, email, role, created_at) VALUES
(1, 'admin', '$2a$10$vvUX82CxXPBaRzjnp6i9y.GqNM5SkndsmNWNx4qNsXWLSKWqnlvg2', 'admin@example.com', 'ADMIN', '2025-04-23 21:52:58'),
(2, 'TinMoz', '$2a$10$qiQ51w7oOw/lXP9QGohUA.kuQqrhkQe1bV80pJfUXDzMbeav71Vx2', 'tinmozpoon2@gmail.com', 'USER', '2025-04-23 21:57:30'),
(11, 'Admin02', '$2a$10$Y8CRk9dj9PExqqeHyjvlyeS47cgHTLy7fHx2wc7lR092b6PA4kFXm', 'Admin02@example.com', 'ADMIN', '2025-04-24 01:00:12'),
(12, 'Test', '$2a$10$SFVzhas.0dUO.lJk51ZkueQFzQsQm8EemO8N9KobMVt71BJDHIEEC', 'Test@gamil.com', 'USER', '2025-04-27 21:35:29'),
(13, 'testuser', '$2a$10$szx4F38J7WT7VZFU6uDAQOtiLV6LUA.1Mufnu/kXIgfinMya3O52K', 'test@example.com', 'USER', '2025-05-05 17:07:38'),
(14, 'Liam_Johnson32', '$2a$10$ByKLmwX85eljNNsCTnGsg.Z5rCEBL/MCeA6cId5eTiUhGxBmg4gDC', 'liam.johnson32@mailservice.com', 'USER', '2025-05-05 17:08:57'),
(15, 'Emma_Wilson_7', '$2a$10$UhDrX5g6jp4eXsAyDH3qtOE7L0dP6rExa88.y5Y2e/xeXYCu/tX.a', 'emma.wilson7@example.net', 'USER', '2025-05-05 17:09:33'),
(16, 'NoahBrown_Dev', '$2a$10$a3/aVwC0clZzC9hJ.E5Cker2lJHpcMFqVfNd5QXK64xUH6DjgPk1K', 'noah.b.dev@testdomain.org', 'USER', '2025-05-05 17:10:56'),
(17, 'OliviaSmith_QA', '$2a$10$buZzh.XgyAQw/jSoujt61.nD0ZBVVopzNnUxZfHf3RCai/vTIqnOa', 'olivia.qa@demoemail.io', 'USER', '2025-05-05 17:11:18'),
(18, 'LucasMiller_2024', '$2a$10$KCVbxBNielfNTJKuStRUSunFt.sO.lOgKyrBoVaFQwpSD3hq/x2om', 'lucas.m24@tempinbox.com', 'USER', '2025-05-05 17:11:39'),
(19, 'AvaDavis_Support', '$2a$10$aGsAOPX/q9RrF/R0.uvr1e9xn/P0PZEk9tj7TxsU2EtbX7mGyuzY2', 'ava.support@fakemail.co', 'USER', '2025-05-05 17:11:56'),
(20, 'EthanTaylor_Admin', '$2a$10$mGV7bwu0rg24/qf9UJO8IehlPvdtsC/O1teoueel58UlhaGU11KlG', 'ethan.t.admin@mockupmail.net', 'USER', '2025-05-05 17:12:26'),
(21, 'MiaClark_Design', '$2a$10$Ftp/azmdRXzTWSZC6ZYAdO7B00kV/FY0coiaAyOYndQ9/xfNJYb3K', 'mia.design@fauxmail.org', 'USER', '2025-05-05 17:13:13'),
(22, 'JamesLee_DevOps', '$2a$10$wNazTRdFQ8Mp7tB6p.y2AeSj6drqWSETO6XiSbjUjxnp5T52bRrK2', 'james.lee@devmail.io', 'USER', '2025-05-05 17:13:31'),
(23, 'SophiaMartin_Data', '$2a$10$QW5n3XkZvMnj6.fTjdfuzuAOIWYHJJWN6MtUx1.v8Aaq3KU5ev5XC', 'sophia.data@samplemail.com', 'USER', '2025-05-05 17:13:56'),
(24, 'AAAA', '$2a$10$UPdL7aEO7yFXXNoUvZIQkueAJCIfYzLJN4xfkUPfKuORFMwAN9YPa', 'AAAAA@gmail.com', 'USER', '2025-05-05 17:27:42');

-- 創建 courses 表
CREATE TABLE courses (
  id int NOT NULL AUTO_INCREMENT,
  title varchar(200) NOT NULL,
  description text NOT NULL,
  course_code varchar(255) DEFAULT NULL,
  credits int DEFAULT NULL,
  created_by int NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  teacher varchar(255) DEFAULT NULL COMMENT '教師全名（中文）',
  PRIMARY KEY (id),
  KEY created_by (created_by),
  KEY idx_title (title),
  CONSTRAINT courses_ibfk_1 FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4;

-- 插入 courses 數據
INSERT INTO courses (id, title, description, course_code, credits, created_by, created_at, teacher) VALUES
(2, '信息安全基础', '涵盖信息安全的基本原则和实践，保护公司数据安全', 'SEC-201', 2, 1, '2023-01-20 10:00:00', '张伟老师'),
(3, '高效沟通技巧', '提升职场沟通能力，包括书面和口头沟通', 'COM-102', 2, 1, '2023-02-05 09:30:00', '李晓红老师'),
(4, '项目管理基础', '介绍项目管理的基本概念和方法论', 'PM-301', 4, 1, '2023-02-15 11:00:00', '王建国老师'),
(5, '领导力发展', '为潜在领导者提供必要的领导技能培训', 'LD-401', 5, 1, '2023-03-01 13:45:00', '陈思思老师'),
(41, '新员工入职培训', '为新员工提供公司文化、政策和基本技能的全面介绍', 'ONB-101', 3, 11, '2025-04-30 21:02:55', '周华老师'),
(56, '高效会议管理', '提升组织和管理高效会议的能力', 'MGT-712', 2, 1, '2025-05-05 15:32:53', '张伟老师'),
(57, '职场时间管理', '掌握优先级管理和工作效率提升技巧', 'PD-415', 3, 11, '2025-05-05 15:32:53', '李晓红老师'),
(58, '客户服务进阶', '培养卓越客户服务与投诉处理技能', 'CSVC-630', 4, 1, '2025-05-05 15:32:53', '王建国老师'),
(59, '数据分析基础', 'Excel与基础数据可视化实战培训', 'DA-103', 3, 11, '2025-05-05 15:32:53', '陈思思老师'),
(60, '压力情绪管理', '职场压力识别与心理调适方法', 'WELL-205', 2, 1, '2025-05-05 15:32:53', '周华老师'),
(61, '跨部门协作', '打破部门壁垒的高效协作策略', 'CROSS-88', 3, 11, '2025-05-05 15:32:53', '临时教师老师'),
(62, '商务礼仪规范', '职场形象管理与商务场合礼仪', 'BETQ-307', 2, 1, '2025-05-05 15:32:53', '周华老师');

-- 創建 chapters 表
CREATE TABLE chapters (
  id int NOT NULL AUTO_INCREMENT,
  course_id int NOT NULL,
  title varchar(200) NOT NULL,
  content mediumtext NOT NULL,
  duration int DEFAULT NULL COMMENT '分钟',
  order_num int DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_course (course_id),
  CONSTRAINT chapters_ibfk_1 FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4;

-- 完整插入 chapters 數據
INSERT INTO chapters (id, course_id, title, content, duration, order_num) VALUES
(15, 5, '领导力基础', '什么是领导力领导力的核心概念和原则。区别于管理（维持秩序），领导力侧重推动变革。科特的变革八步法强调：建立紧迫感（如展示市场威胁数据）、组建指导联盟、制定愿景战略。情商（EQ）的四大维度：自我认知（定期360度反馈）、自我管理（压力下保持冷静）、社会认知（察觉团队情绪）、关系管理（建设性处理冲突）。', 180, 1),
(16, 5, '团队建设', '建立高效团队如何组建和发展高效团队。应用塔克曼团队发展阶段模型：在组建期（Forming）明确角色，磨合期（Storming）建立冲突解决机制，规范期（Norming）制定协作流程，成熟期（Performing）实施自主管理。通过OKR对齐个人目标与组织战略，例如将"提高代码质量"转化为"Q3单元测试覆盖率≥90%"。定期组织非正式交流（如周五午餐会）增强凝聚力。', 180, 2),
(17, 5, '决策制定', '有效决策领导者的决策方法和技巧。使用Vroom-Yetton决策模型：根据问题重要性、信息完整度、团队接受度等因素，选择从独裁式到共识式等五种决策方式。例如产品重大方向调整需咨询专家委员会（协商式），而部门团建地点选择可授权团队投票（委派式）。引入魔鬼代言人机制，强制挑战每个方案的潜在缺陷。', 180, 3),
(18, 5, '变革管理', '引领变革如何有效管理和引领组织变革。运用ADKAR模型：先建立意识（全员会议说明变革必要性）、培养意愿（高潜力员工提前参与试点）、提供知识（定制化培训）、强化能力（导师制支持）、持续巩固（将新KPI纳入考核）。例如ERP系统升级时，为关键用户提供200小时的情景模拟训练，设立"变革冠军"奖励机制。', 150, 4),
(19, 5, '战略思维', '战略规划发展战略性思维和规划能力。通过PESTEL分析宏观环境（如监管政策变化），波特五力模型评估行业竞争力。使用蓝海战略寻找价值创新点，例如将传统银行服务与金融科技结合推出智能投顾。制定3年滚动战略时，需包含应急情景规划（Scenario Planning），预设经济衰退、技术颠覆等不同情境的应对方案。', 210, 5),
(33, 41, '公司介绍', '欢迎加入我们公司本节将介绍公司历史、文化和价值观。公司成立于1998年，从本地软件开发起步，现已发展为跨国科技解决方案提供商，在15个国家设有创新中心。核心文化CODE价值观：协作（Collaboration）、开放（Openness）、担当（Dedication）、卓越（Excellence）。例如每月"创新星期五"鼓励跨部门组队提案，年度最佳创意可获得$50k孵化基金。', 150, 1),
(34, 41, '人力资源政策', '人力资源政策了解休假、福利和员工行为准则。年假按职级累计（初级员工15天/年，每晋升一级+3天），病假需提前2小时在HR系统报备。福利包含补充医疗保险（覆盖配偶/子女）、学费报销（与岗位相关硕士课程最高$10k/年）、健身房津贴（$50/月）。严禁行为包括利益冲突未申报（如亲属在竞对公司任职）、使用盗版软件等，违者可能面临纪律处分。', 180, 2),
(35, 41, 'IT系统入门', 'IT系统使用指南邮箱、内部系统和常用工具的使用方法。企业邮箱格式为姓名缩写+入职年份（如zwang23@company.com），首次登录需在Okta设置SSO单点登录。财务系统（SAP FICO）报销流程：每周四前提交带主管审批码的PDF发票，5个工作日内到账。代码仓库需遵守GitFlow规范，生产环境部署必须通过Jenkins流水线，严禁直接修改服务器配置。', 150, 3),
(36, 2, '信息安全概述', '什么是信息安全介绍信息安全的基本概念和重要性。信息安全涉及保护信息系统免受未经授权的访问、使用、披露、破坏、修改或销毁，涵盖网络安全、数据加密、身份验证等领域。在数字化时代，信息安全对于保护企业商业秘密、客户隐私以及遵守GDPR等数据保护法规至关重要。例如，一次数据泄露可能导致数百万经济损失和品牌声誉受损。', 120, 1),
(37, 2, '密码管理', '创建强密码如何创建和管理安全的密码。强密码应包含至少12个字符，混合大小写字母、数字和符号（如@#%&），避免使用生日、姓名等易猜信息。建议采用密码管理器（如LastPass）生成并存储密码，定期每90天更换一次。同时启用多因素认证（MFA），例如通过手机验证码或生物识别技术（指纹/面部识别）增加安全层级。', 150, 2),
(38, 2, '防范网络钓鱼', '识别网络钓鱼如何识别和避免网络钓鱼攻击。注意可疑邮件的典型特征：发件人地址拼写错误（如paypa1.com）、紧急威胁性语言（"立即验证账户"）、包含短链接或异常附件。建议通过企业安全平台验证链接真实性，切勿直接点击。例如，收到"银行账户异常"邮件时，应直接登录官网而非点击邮件中的链接。定期参加钓鱼模拟演练可提升员工识别能力。', 120, 3),
(39, 2, '数据保护', '保护敏感数据正确处理公司敏感数据的方法。根据数据分类政策，机密文件（如客户合同、财务报告）必须存储在加密的共享驱动器（如SharePoint安全区），禁止通过公共云盘传输。使用数据丢失防护（DLP）工具监控外发数据，打印纸质文件需使用带水印的安全打印机，废弃文件必须用碎纸机销毁。远程办公时需通过VPN访问数据库，并遵守屏幕隐私过滤器使用规范。', 210, 4),
(47, 3, '沟通基础', '沟通的基本原则有效沟通的关键要素。遵循7-38-55法则：7%靠语言内容，38%取决于语调，55%来自肢体语言。保持眼神交流，使用开放式问题（如"您认为这个方案如何改进？"）促进对话。注意文化差异，例如在跨文化团队中，直接表达建议可能更适合欧美成员，而对亚洲成员宜采用更委婉的方式。关键信息应通过邮件书面确认，避免仅依赖口头沟通。', 210, 1),
(48, 3, '书面沟通', '专业邮件写作如何撰写清晰专业的商务邮件。采用"金字塔结构"：首段说明核心目的（如审批请求），中间段落提供背景数据和支撑材料，结尾明确行动项和截止时间。使用专业签名档（含姓名/职位/联系方式），附件命名需规范（如"Q3销售报告_张三_202308.pdf"）。避免使用感叹号和俚语，敏感信息需加密或通过安全渠道发送。', 210, 2),
(49, 3, '会议技巧', '高效会议如何准备和参与高效的会议。会前24小时发送包含议程文件（使用模板DOC-023），明确每个议题的时间分配。使用"停车场法则"记录偏离主题的讨论项。例如："关于预算分配的问题我们记入停车场，下周财务专项会讨论"。会议最后5分钟总结行动项（Who/What/When），使用协同工具（如Teams任务分配）跟踪进度，避免重复会议。', 180, 3),
(62, 4, '项目管理概述', '什么是项目管理项目管理的基本概念和生命周期。采用PMI定义的五大过程组：启动（制定章程）、规划（WBS分解）、执行（资源调配）、监控（风险看板）、收尾（经验复盘）。关键路径法（CPM）帮助识别任务依赖关系，例如软件开发需先完成需求分析才能进入编码阶段。使用甘特图可视化进度，每周通过EVM（挣值分析）评估CPI/SPI指标。', 150, 1),
(63, 4, '项目规划', '创建项目计划如何制定有效的项目计划。运用SMART原则设定目标：将"提高客户满意度"转化为"9月前将NPS评分从70提升至85"。资源计划需包含RACI矩阵明确责任人，风险登记册应评估每个风险的概率/影响值（如供应商延迟交付：概率30%/影响$50k）。建议预留10-15%的应急储备，使用JIRA等工具自动化跟踪任务完成率。', 210, 2),
(64, 4, '风险管理', '识别和管理风险项目风险管理的方法。实施四步流程：识别（SWOT分析）、评估（风险矩阵分级）、应对（规避/转移/减轻/接受）、监控（建立风险触发阈值）。例如针对关键技术故障风险，可同时与两家云服务商签订SLA（风险转移），每日备份数据到异地灾备中心（风险减轻）。使用蒙特卡洛模拟量化整体风险敞口。', 210, 3),
(65, 4, '团队协作', '管理项目团队如何有效领导项目团队。运用情境领导理论：对新成员采用指导型风格（详细任务说明），对资深成员授权型管理（仅把控关键节点）。通过每周15分钟的一对一沟通了解成员瓶颈，使用Belbin团队角色测试优化分工。冲突解决可尝试托马斯-基尔曼模型，例如对紧急技术争议采用竞争策略，对长期合作问题采用协作策略。', 180, 4),
(104, 56, '会议规划与议程设计', '高效会议的第一步是明确目标和设计议程。采用RACI矩阵明确参会者角色（负责人、执行人、咨询人、知情人），例如技术评审会需邀请架构师（负责人）和测试组长（咨询人）。议程模板应包含：目标声明、议题列表（每个议题分配时间）、预期产出。使用协作工具（如Confluence）提前24小时共享预读材料，并标注决策类议题（需会前反馈初步意见）。', 150, 1),
(105, 56, '会议执行与决策跟进', '主持人需严格遵循议程时间，使用"两分钟规则"控制发散讨论。对于决策类议题，应用阶梯式决策法：先收集书面建议（匿名投票工具如Mentimeter），再公开讨论，最后负责人确认。会议记录需实时共享（如使用钉钉妙记），行动项明确责任人/截止日（例如"张三在5月20日前完成API文档修订"）。会后1小时内发送总结邮件，并同步至JIRA任务看板。', 180, 2),
(106, 57, '优先级矩阵应用', '使用艾森豪威尔矩阵划分任务：紧急且重要（立即处理，如服务器故障）、重要不紧急（计划处理，如代码重构）、紧急不重要（委托处理，如会议邀约）、不紧急不重要（批量处理或删除）。例如每日晨会前花10分钟用Trello看板分类任务，对"重要不紧急"类任务固定安排深度工作时间块（如14:00-16:00关闭通讯工具）。', 135, 1),
(107, 57, '时间阻塞与干扰管理', '采用番茄工作法（25分钟专注+5分钟休息），配合物理标识（如红色帽子表示勿扰）。应对突发干扰时，应用"2D原则"：立即处理（Do it now）或延迟到下一时间块（Delay）。例如收到临时需求邮件，若非关键则回复"已记录，将在今日15:00批次处理"。使用RescueTime监控数字干扰源，限制社交软件日均使用≤30分钟。', 150, 2),
(108, 57, 'GTD工作流实践', '构建GTD系统：收集篮（Inbox）统一收纳任务（便签/邮件/语音备忘录），每周五下午进行清理加工。对复杂任务采用"下一步行动"拆解，例如"开发支付模块"拆分为"1. 确认接口文档版本（30分钟）2. 编写单元测试用例（2小时）"。使用Evernote建立情景标签（@办公室/@通勤），快速匹配碎片时间可利用任务。', 180, 3),
(109, 58, '客户需求深度挖掘', '应用SPIN提问法：现状问题（如"当前系统响应速度如何？"）、痛点问题（"延迟对业务的影响是？"）、暗示问题（"若持续会影响客户满意度吗？"）、需求-回报问题（"如果提速30%会带来什么价值？"）。配合主动倾听技巧：重复客户关键词（"您刚提到结算延迟..."），观察非语言信号（如语气急促时优先安抚情绪）。', 210, 1),
(110, 58, '投诉升级处理流程', '执行L.A.S.T.原则：倾听（Listen）- 记录客户原话；道歉（Apologize）- 即便非己方过错也表达歉意；解决（Solve）- 提供3选1方案（退款/换货/代金券）；跟进（Track）- 24小时内电话回访。例如针对物流损坏投诉，优先发送带公章道歉信，同步启动内部质量追溯（通过SN码定位环节责任）。', 180, 2),
(111, 58, '服务质量管理闭环', '建立NPS（净推荐值）监控体系，对贬损者（0-6分）48小时内深度回访。服务质量雷达图每月更新，评估5维度：响应速度/专业度/解决率/礼貌性/创新建议。开展"影子客户"项目：员工每季度匿名体验竞品服务，提交SWOT分析报告。针对重复投诉问题，启动根因分析（5Why法）并纳入KPI改进计划。', 210, 3),
(112, 59, '数据清洗与预处理', '掌握Excel数据清洗核心技巧：使用Power Query去除重复值（保留最后出现记录）、处理缺失值（数值型字段用中位数填充，文本型标记为[未分类]）。日期统一转换为ISO 8601格式（YYYY-MM-DD），异常值通过条件格式突出显示（如销售额>3倍标准差标红）。创建数据验证规则（如部门字段仅允许下拉选择"市场/技术/财务"），导出清洗日志供后续审计。', 180, 1),
(113, 59, '高级公式与函数', '实战复杂场景公式嵌套：使用INDEX-MATCH实现多条件查找（如按季度+产品线匹配销量），XLOOKUP替代VLOOKUP解决反向查找问题。数组公式计算动态排名（=SUMPRODUCT((B2:B100>B2)/COUNTIF(B2:B100,B2:B100))+1）。利用LAMBDA自定义函数（如创建税率计算器TAX_CALC），通过名称管理器实现跨工作表调用。', 210, 2),
(114, 59, '可视化仪表盘搭建', '构建动态交互仪表盘：使用切片器控制多个透视表（如按区域/时间筛选销售数据），设置条件格式数据条反映库存周转率。开发瀑布图展示成本构成（正负值自动颜色区分），利用Power Map实现地理数据三维可视化。通过窗体控件（滚动条/选项按钮）创建动态图表参数，最终输出带密码保护的仪表盘模板。', 210, 3),
(115, 60, '压力信号识别技术', '识别生理与心理压力信号：通过心率变异度（HRV）检测器监测压力水平（<50ms为高压力状态），记录情绪日记标记触发事件（如会议冲突/截止日期）。使用压力矩阵图区分压力源类型（任务型/关系型/环境型），应用"压力温度计"量表（1-10分）每日自评，当连续3天≥7分时启动干预流程。', 150, 1),
(116, 60, '认知重构与放松训练', '实施ABC模型干预：捕捉自动负面思维（如"项目失败=能力不足"），替换为理性认知（"需要加强风险规划"）。结合渐进式肌肉放松法（PMR）：按头→肩→手顺序紧张-放松循环，配合4-7-8呼吸法（吸气4秒、屏息7秒、呼气8秒）。使用生物反馈设备训练α脑波生成，每天15分钟正念冥想巩固效果。', 180, 2),
(117, 61, '利益相关者分析', '绘制跨部门权力-利益矩阵：识别高权力高利益部门（如财务部预算审批），为其定制双周简报。建立RACI-VS模型（新增Verify验证者和Signatory签署方），例如产品上线需法务部验证合规性（V角色）。通过利益交换策略解决资源冲突（如用技术部API接口支持换取市场部推广资源）。', 210, 1),
(118, 61, '协作沟通框架', '采用DISC模型适配沟通风格：对支配型（D型）上级用结论先行+数据支撑，对影响型（I型）同事加入情感共鸣。建立跨部门术语对照表（如技术部"迭代"=业务部"版本更新"）。使用Confluence创建协作空间，设置自动同步机制（财务流程变更实时推送关联部门）。每月举办"流程痛点头脑风暴"工作坊。', 180, 2),
(119, 61, '冲突解决沙盘演练', '模拟典型冲突场景：资源争夺（如服务器资源分配），应用托马斯-基尔曼模型选择策略。对紧急技术决策采用竞争模式（引用ISO标准支撑），对长期合作问题启用调解人机制。开发冲突影响计算表（含时间成本/关系损伤/业务损失权重），当总分>阈值时升级至steering committee。', 210, 3),
(120, 62, '职业形象管理', '制定场合着装密码：商务正式（男士深色套装+法式袖扣，女士套裙+裸色高跟鞋）、商务休闲（POLO衫+休闲西裤）。细节检查清单包含：袖长（西装露出1cm衬衫）、领带长度（尖部对齐皮带扣）、妆容浓度（眼影不超过双色）。建立应急形象包（备用衬衫/去渍笔/迷你熨斗），应对突发状况。', 150, 1),
(121, 62, '国际商务礼仪', '掌握跨文化礼仪核心差异：日本客户交换名片双手接收并默读内容，中东客户避免左手递物。宴请礼仪包含：法式餐叉由外至内使用，红酒斟倒不超过1/3杯。会议座位遵循"面门为尊"，使用Webex虚拟背景统一团队形象。邮件签名附加文化备注（如对德国客户注明精确时间格式"14:00 CET"）。', 180, 2);

-- 創建 learning_progress 表
CREATE TABLE learning_progress (
  id int NOT NULL AUTO_INCREMENT,
  user_id int NOT NULL,
  course_id int NOT NULL,
  chapter_id int NOT NULL,
  Completed tinyint(1) DEFAULT '0',
  progress int DEFAULT '0',
  last_accessed timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_chapter (user_id, chapter_id),
  KEY idx_user (user_id),
  KEY idx_chapter (chapter_id),
  KEY fk_course (course_id),
  CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
  CONSTRAINT learning_progress_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT learning_progress_ibfk_2 FOREIGN KEY (chapter_id) REFERENCES chapters (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4;

--插入 learning_progress數據
-- 用户2 (TinMoz)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(2,41,35,1,100,'2025-05-05 08:23:17'),
(2,5,19,1,100,'2025-05-07 14:55:02'),
(2,57,108,0,65,'2025-05-12 19:31:48');

-- 用户12 (Test)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(12,41,35,1,100,'2025-05-01 11:11:39'),
(12,61,119,1,100,'2025-05-03 16:47:22'),
(12,58,111,0,80,'2025-05-09 09:15:57'),
(12,56,104,0,45,'2025-05-15 22:03:18');

-- 用户13 (testuser)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(13,41,35,1,100,'2025-04-30 07:32:14'),
(13,60,116,1,100,'2025-05-06 13:29:05'),
(13,4,65,0,30,'2025-05-10 18:12:49');

-- 用户14 (Liam_Johnson32)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(14,41,35,1,100,'2025-05-02 10:44:36'),
(14,2,39,1,100,'2025-05-08 15:19:27'),
(14,59,114,0,95,'2025-05-14 20:55:11'),
(14,56,105,0,60,'2025-05-18 08:07:53');

-- 用户15 (Emma_Wilson_7)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(15,41,35,1,100,'2025-05-03 09:17:28'),
(15,3,49,1,100,'2025-05-11 12:33:44'),
(15,57,107,0,75,'2025-05-16 17:21:09');

-- 用户16 (NoahBrown_Dev)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(16,41,35,1,100,'2025-05-04 14:08:52'),
(16,62,121,1,100,'2025-05-13 19:44:35'),
(16,5,19,0,85,'2025-05-17 21:37:16');

-- 用户17 (OliviaSmith_QA)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(17,41,35,1,100,'2025-05-05 16:29:43'),
(17,58,110,1,100,'2025-05-07 10:55:21'),
(17,4,65,0,50,'2025-05-19 23:15:38');

-- 用户18 (LucasMiller_2024)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(18,41,35,1,100,'2025-05-06 08:47:19'),
(18,59,114,1,100,'2025-05-12 14:22:05'),
(18,56,105,0,70,'2025-05-20 09:33:57');

-- 用户19 (AvaDavis_Support)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(19,41,35,1,100,'2025-05-07 12:38:24'),
(19,60,116,1,100,'2025-05-14 17:14:07'),
(19,61,118,0,90,'2025-05-21 22:45:39');

-- 用户20 (EthanTaylor_Admin)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(20,41,35,1,100,'2025-05-08 15:51:12'),
(20,2,39,1,100,'2025-05-16 20:26:53'),
(20,57,107,0,55,'2025-05-22 11:08:44');

-- 用户21 (MiaClark_Design)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(21,41,35,1,100,'2025-05-09 18:03:27'),
(21,3,49,1,100,'2025-05-17 21:49:15'),
(21,62,120,0,40,'2025-05-23 14:22:36');

-- 用户22 (JamesLee_DevOps)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(22,41,35,1,100,'2025-05-10 19:14:58'),
(22,5,19,1,100,'2025-05-18 22:50:31'),
(22,58,111,0,65,'2025-05-24 16:37:49');

-- 用户23 (SophiaMartin_Data)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(23,41,35,1,100,'2025-05-11 20:25:43'),
(23,59,114,1,100,'2025-05-19 23:11:26'),
(23,56,104,0,75,'2025-05-25 18:52:17');

-- 用户24 (AAAAA)
INSERT INTO learning_progress (user_id, course_id, chapter_id, Completed, progress, last_accessed) VALUES
(24,41,35,1,100,'2025-05-12 21:36:54'),
(24,4,65,1,100,'2025-05-20 22:22:38'),
(24,60,116,0,85,'2025-05-26 19:43:29');
