package top.tangtian.esanalysis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tangtian
 * @date 2025-12-01 09:43
 */
@Data
public class FeedbackEntity {

	private Long id;

	private String title;

	private String content;

	private String username;

	@JsonProperty("passport_id")
	private String passportId;

	@JsonProperty("wechat_uid")
	private String wechatUid;

	private String source;

	@JsonProperty("handle_status")
	private String handleStatus;

	@JsonProperty("handle_status_real")
	private String handleStatusReal;

	@JsonProperty("reply_status")
	private String replyStatus;

	@JsonProperty("visible_status")
	private String visibleStatus;

	@JsonProperty("expire_flag")
	private String expireFlag;

	@JsonProperty("organization_name")
	private String organizationName;

	@JsonProperty("field_name")
	private String fieldName;

	@JsonProperty("sort_name")
	private String sortName;

	private String creator;

	private String updator;

	@JsonProperty("cgxw_username")
	private String cgxwUsername;

	@JsonProperty("area_id")
	private Integer areaId;

	@JsonProperty("assign_organization_id")
	private Integer assignOrganizationId;

	@JsonProperty("chosen_organization_id")
	private Integer chosenOrganizationId;

	@JsonProperty("field_id")
	private Integer fieldId;

	@JsonProperty("sort_id")
	private Integer sortId;

	@JsonProperty("origin_id")
	private Long originId;

	@JsonProperty("cgxw_user_id")
	private Long cgxwUserId;

	private Integer satisfaction;

	@JsonProperty("info_hidden")
	private Integer infoHidden;

	@JsonProperty("has_video")
	private Integer hasVideo;

	@JsonProperty("delete_at")
	private Long deleteAt;

	@JsonProperty("apply_postpone_flag")
	private Boolean applyPostponeFlag;

	@JsonProperty("apply_satisfaction_flag")
	private Boolean applySatisfactionFlag;

	@JsonProperty("apply_transfer_flag")
	private Boolean applyTransferFlag;

	@JsonProperty("can_feedback_flag")
	private Boolean canFeedbackFlag;

	@JsonProperty("warn_flag")
	private Boolean warnFlag;

	private String ip;

	private String link;

	private List<String> attaches;

	private List<String> tags;

	private String comments;

	private String video;

	@JsonProperty("cgxw_article_ids")
	private List<String> cgxwArticleIds;

	@JsonProperty("created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date createdAt;

	@JsonProperty("updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date updatedAt;

	@JsonProperty("assign_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date assignAt;

	@JsonProperty("handle_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date handleAt;

	@JsonProperty("reply_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date replyAt;

	@JsonProperty("done_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date doneAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date deadline;

	private Map<String, Object> ext;

	@JsonProperty("tags_info")
	private Map<String, Object> tagsInfo;

	@JsonProperty("full_org_path_nodes")
	private Map<String, Object> fullOrgPathNodes;
}
