package am.ik.surveys.role;

public enum Resource {

	WILDCARD {
		@Override
		public String toString() {
			return "*";
		}
	},
	ORGANIZATION_USER, SURVEY, QUESTION_GROUP, QUESTION, QUESTION_CHOICE, SURVEY_QUESTION_GROUP,
	QUESTION_GROUP_QUESTION, ANSWER;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public static Resource valueFrom(String s) {
		return Resource.valueOf(s.toUpperCase().replace("*", WILDCARD.name()));
	}

}
