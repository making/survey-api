package am.ik.surveys.questiongroup.web;

import java.net.URI;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(path = "/question_groups")
public class QuestionGroupController {

	private final QuestionGroupRepository questionGroupRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionGroupController(QuestionGroupRepository questionGroupRepository, TsidGenerator tsidGenerator) {
		this.questionGroupRepository = questionGroupRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "")
	public List<QuestionGroup> getQuestionGroups() {
		return this.questionGroupRepository.findAll();
	}

	@PostMapping(path = "")
	public ResponseEntity<QuestionGroup> postQuestionGroups(@RequestBody QuestionGroupRequest request,
			UriComponentsBuilder builder) {
		final QuestionGroupId questionChoiceId = new QuestionGroupId(this.tsidGenerator.generate());
		final QuestionGroup questionGroup = request.toQuestionGroup(questionChoiceId);
		this.questionGroupRepository.insert(questionGroup);
		final URI location = builder.replacePath("/question_groups/{questionGroupId}")
			.build(questionChoiceId.asString());
		return ResponseEntity.created(location).body(questionGroup);
	}

	@GetMapping(path = "/{questionGroupId}")
	public QuestionGroup getQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		return this.questionGroupRepository.findById(questionGroupId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question group id is not found (%s)".formatted(questionGroupId.asString())));
	}

	@DeleteMapping(path = "/{questionGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		this.questionGroupRepository.deleteById(questionGroupId);
	}

}