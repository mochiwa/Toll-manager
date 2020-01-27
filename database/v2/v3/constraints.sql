ALTER TABLE public.phone
	ADD CONSTRAINT phone_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.email
	ADD CONSTRAINT email_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_cities0_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.situated
	ADD CONSTRAINT situated_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_country0_FK
	FOREIGN KEY (country_id)
	REFERENCES public.country(country_id);

ALTER TABLE public.located
	ADD CONSTRAINT located_cities1_FK
	FOREIGN KEY (city_id)
	REFERENCES public.cities(city_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.lives
	ADD CONSTRAINT lives_address1_FK
	FOREIGN KEY (address_id)
	REFERENCES public.address(address_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_rule0_FK
	FOREIGN KEY (rule_id)
	REFERENCES public.rule(rule_id);

ALTER TABLE public.hasRules
	ADD CONSTRAINT hasRules_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.belong
	ADD CONSTRAINT belong_groups1_FK
	FOREIGN KEY (group_id,group_name)
	REFERENCES public.groups(group_id,group_name);

ALTER TABLE public.team
	ADD CONSTRAINT team_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.team
	ADD CONSTRAINT team_team1_FK
	FOREIGN KEY (team_id_parent)
	REFERENCES public.team(team_id);

ALTER TABLE public.Planning
	ADD CONSTRAINT Planning_employee0_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);

ALTER TABLE public.Planning
	ADD CONSTRAINT Planning_team1_FK
	FOREIGN KEY (team_id)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_team0_FK
	FOREIGN KEY (team_id)
	REFERENCES public.team(team_id);

ALTER TABLE public.isMember
	ADD CONSTRAINT isMember_employee1_FK
	FOREIGN KEY (employee_id)
	REFERENCES public.employee(employee_id);
