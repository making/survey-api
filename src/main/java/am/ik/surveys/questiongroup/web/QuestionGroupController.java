package am.ik.surveys.questiongroup.web;

import java.net.URI;
import java.util.List;
import java.util.Set;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupRepository;
import am.ik.surveys.tsid.TsidGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class QuestionGroupController {

	private final QuestionGroupHandler questionGroupHandler;

	private final QuestionGroupRepository questionGroupRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionGroupController(QuestionGroupHandler questionGroupHandler,
			QuestionGroupRepository questionGroupRepository, TsidGenerator tsidGenerator) {
		this.questionGroupHandler = questionGroupHandler;
		this.questionGroupRepository = questionGroupRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "/organizations/{organizationId}/question_groups")
	public List<QuestionGroup> getQuestionGroups(@PathVariable OrganizationId organizationId) {
		return this.questionGroupRepository.findByOrganizationId(organizationId);
	}

	@GetMapping(path = "/question_groups", params = "question_group_ids")
	public List<QuestionGroupResponse> getQuestionGroups(
			@RequestParam(name = "question_group_ids") Set<QuestionGroupId> questionGroupIds) {
		return this.questionGroupHandler.getQuestionGroups(questionGroupIds);
	}

	@PostMapping(path = "/organizations/{organizationId}/question_groups")
	public ResponseEntity<QuestionGroup> postQuestionGroups(@PathVariable OrganizationId organizationId,
			@RequestBody QuestionGroupRequest request, UriComponentsBuilder builder) {
		final QuestionGroupId questionChoiceId = new QuestionGroupId(this.tsidGenerator.generate());
		final QuestionGroup questionGroup = request.toQuestionGroup(questionChoiceId, organizationId);
		this.questionGroupRepository.insert(questionGroup);
		final URI location = builder.replacePath("/question_groups/{questionGroupId}")
			.build(questionChoiceId.asString());
		return ResponseEntity.created(location).body(questionGroup);
	}

	@GetMapping(path = "/question_groups/{questionGroupId}")
	public QuestionGroupResponse getQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		return this.questionGroupHandler.getQuestionGroup(questionGroupId);
	}

	@DeleteMapping(path = "/question_groups/{questionGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		this.questionGroupRepository.deleteById(questionGroupId);
	}

}
