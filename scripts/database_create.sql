DROP SCHEMA IF EXISTS professionals CASCADE;

CREATE SCHEMA professionals;

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS plpython3u;


CREATE TABLE professionals.PersistentTokens (
                Series VARCHAR NOT NULL,
                Value VARCHAR NOT NULL,
                Email VARCHAR NOT NULL,
                Date DATE NOT NULL,
                IpAddress VARCHAR,
                UserAgent VARCHAR NOT NULL,
                CONSTRAINT series PRIMARY KEY (Series)
);


CREATE SEQUENCE professionals.items_itemid_seq;

CREATE TABLE professionals.Items (
                ItemId INTEGER NOT NULL DEFAULT nextval('professionals.items_itemid_seq'),
                CategoryType VARCHAR,
                Name VARCHAR NOT NULL,
                FullName VARCHAR NOT NULL,
                CONSTRAINT itemid PRIMARY KEY (ItemId)
);


ALTER SEQUENCE professionals.items_itemid_seq OWNED BY professionals.Items.ItemId;

CREATE SEQUENCE professionals.tags_tagid_seq;

CREATE TABLE professionals.Tags (
                TagId INTEGER NOT NULL DEFAULT nextval('professionals.tags_tagid_seq'),
                TagName VARCHAR NOT NULL,
                TagLabel VARCHAR NOT NULL,
                CONSTRAINT tagid PRIMARY KEY (TagId)
);


ALTER SEQUENCE professionals.tags_tagid_seq OWNED BY professionals.Tags.TagId;

CREATE TABLE professionals.TagsForItems (
                TagId INTEGER NOT NULL,
                ItemId INTEGER NOT NULL,
                CONSTRAINT tagforitemid PRIMARY KEY (TagId, ItemId)
);


CREATE SEQUENCE professionals.professions_professionid_seq;

CREATE TABLE professionals.Professions (
                ProfessionId INTEGER NOT NULL DEFAULT nextval('professionals.professions_professionid_seq'),
                ItemId INTEGER NOT NULL,
                CONSTRAINT professionid PRIMARY KEY (ProfessionId)
);


ALTER SEQUENCE professionals.professions_professionid_seq OWNED BY professionals.Professions.ProfessionId;

CREATE SEQUENCE professionals.servicecategories_servicecategoryid_seq;

CREATE TABLE professionals.ServiceCategories (
                ServiceCategoryId INTEGER NOT NULL DEFAULT nextval('professionals.servicecategories_servicecategoryid_seq'),
                ItemId INTEGER NOT NULL,
                ProfessionId INTEGER NOT NULL,
                CONSTRAINT servicecategoryid PRIMARY KEY (ServiceCategoryId)
);


ALTER SEQUENCE professionals.servicecategories_servicecategoryid_seq OWNED BY professionals.ServiceCategories.ServiceCategoryId;

CREATE SEQUENCE professionals.services_serviceid_seq;

CREATE TABLE professionals.Services (
                ServiceId INTEGER NOT NULL DEFAULT nextval('professionals.services_serviceid_seq'),
                ItemId INTEGER NOT NULL,
                ServiceCategoryId INTEGER NOT NULL,
                CONSTRAINT serviceid PRIMARY KEY (ServiceId)
);
COMMENT ON TABLE professionals.Services IS 'Usluge';


ALTER SEQUENCE professionals.services_serviceid_seq OWNED BY professionals.Services.ServiceId;

CREATE SEQUENCE professionals.counties_countyid_seq;

CREATE TABLE professionals.Counties (
                CountyId INTEGER NOT NULL DEFAULT nextval('professionals.counties_countyid_seq'),
                Name VARCHAR NOT NULL,
                Path VARCHAR,
                CONSTRAINT countyid PRIMARY KEY (CountyId)
);


ALTER SEQUENCE professionals.counties_countyid_seq OWNED BY professionals.Counties.CountyId;

CREATE SEQUENCE professionals.districts_districtid_seq;

CREATE TABLE professionals.Districts (
                DistrictId INTEGER NOT NULL DEFAULT nextval('professionals.districts_districtid_seq'),
                Name VARCHAR NOT NULL,
                Path VARCHAR NOT NULL,
                CountyId INTEGER NOT NULL,
                CONSTRAINT districtid PRIMARY KEY (DistrictId)
);


ALTER SEQUENCE professionals.districts_districtid_seq OWNED BY professionals.Districts.DistrictId;

CREATE SEQUENCE professionals.localities_localityid_seq;

CREATE TABLE professionals.Localities (
                LocalityId INTEGER NOT NULL DEFAULT nextval('professionals.localities_localityid_seq'),
                Name VARCHAR NOT NULL,
                PostalCode INTEGER,
                DistrictId INTEGER NOT NULL,
                CountyId INTEGER NOT NULL,
                Path VARCHAR NOT NULL,
                CONSTRAINT localityid PRIMARY KEY (LocalityId)
);


ALTER SEQUENCE professionals.localities_localityid_seq OWNED BY professionals.Localities.LocalityId;

CREATE SEQUENCE professionals.addresses_addressid_seq;

CREATE TABLE professionals.Addresses (
                AddressId INTEGER NOT NULL DEFAULT nextval('professionals.addresses_addressid_seq'),
                Lat DOUBLE PRECISION,
                Long DOUBLE PRECISION,
                LatLong GEOMETRY,
                AddressLine VARCHAR,
                County VARCHAR,
                District VARCHAR,
                LocalityId INTEGER,
                CONSTRAINT address_id PRIMARY KEY (AddressId)
);


ALTER SEQUENCE professionals.addresses_addressid_seq OWNED BY professionals.Addresses.AddressId;

CREATE SEQUENCE professionals.users_userid_seq;

CREATE TABLE professionals.Users (
                UserId INTEGER NOT NULL DEFAULT nextval('professionals.users_userid_seq'),
                Name VARCHAR(100) NOT NULL,
                Password VARCHAR NOT NULL,
                Email VARCHAR(255),
                AddressId INTEGER NOT NULL,
                BackgroundImage VARCHAR,
                LeadImage VARCHAR,
                Telephone VARCHAR,
                Mobile VARCHAR,
                Role VARCHAR NOT NULL,
                ActivationKey VARCHAR,
                Active BOOLEAN DEFAULT false NOT NULL,
                ResetPasswordToken VARCHAR,
                ResetPasswordTokenDate DATE,
                CONSTRAINT userid PRIMARY KEY (UserId)
);
COMMENT ON COLUMN professionals.Users.LeadImage IS 'The path to the lead image for a professional, that is stored on the file system of the server.';


ALTER SEQUENCE professionals.users_userid_seq OWNED BY professionals.Users.UserId;

CREATE UNIQUE INDEX email_idx
 ON professionals.Users
 ( Email );

CREATE SEQUENCE professionals.professionals_professionalid_seq;

CREATE TABLE professionals.Professionals (
                ProfessionalId INTEGER NOT NULL DEFAULT nextval('professionals.professionals_professionalid_seq'),
                UserId INTEGER NOT NULL,
                OwnerName VARCHAR,
                Logo VARCHAR,
                Professions VARCHAR,
                PageTitle VARCHAR(65),
                PageUrl VARCHAR(80),
                MetaDescription VARCHAR(160),
                ProfileHeadline VARCHAR,
                ProfileSubHeadline VARCHAR,
                ProfileMoneyShot VARCHAR,
                FeaturesHeadline VARCHAR,
                FeaturesSubHeadline VARCHAR,
                EmployeeCount INTEGER,
                Score INTEGER DEFAULT 0 NOT NULL,
                YearEstablished INTEGER,
                ContactEmail VARCHAR,
                Fax VARCHAR,
                Website VARCHAR,
                ProjectCount INTEGER DEFAULT 0 NOT NULL,
                EndorsementCount INTEGER DEFAULT 0 NOT NULL,
                Description VARCHAR,
                ReviewCount INTEGER DEFAULT 0 NOT NULL,
                AvgReview DOUBLE PRECISION DEFAULT 0 NOT NULL,
                Verified BOOLEAN DEFAULT false NOT NULL,
                SignupDate DATE,
                AcceptsCreditCards BOOLEAN,
                CONSTRAINT professionalid PRIMARY KEY (ProfessionalId)
);
COMMENT ON COLUMN professionals.Professionals.Logo IS 'Url to logo';
COMMENT ON COLUMN professionals.Professionals.PageTitle IS 'The text that will appear in the browser''s title. Important for SEO reasons and usability.';
COMMENT ON COLUMN professionals.Professionals.PageUrl IS 'User friendly professionals'' profile URL. Important for usability, link sharing and SEO.';
COMMENT ON COLUMN professionals.Professionals.MetaDescription IS 'Meta description for search engines. ';
COMMENT ON COLUMN professionals.Professionals.Score IS 'Score for a proffesional, calculated from several variables, refreshed on change of any variable.';
COMMENT ON COLUMN professionals.Professionals.ProjectCount IS 'Number of projects registered on the platform.';
COMMENT ON COLUMN professionals.Professionals.Description IS 'The popular "About" description.';
COMMENT ON COLUMN professionals.Professionals.ReviewCount IS 'Number of reviews registered on the platform for this professional.';
COMMENT ON COLUMN professionals.Professionals.AvgReview IS 'Average review for a professional.';
COMMENT ON COLUMN professionals.Professionals.Verified IS 'Has the information for this professional been verified.';


ALTER SEQUENCE professionals.professionals_professionalid_seq OWNED BY professionals.Professionals.ProfessionalId;

CREATE UNIQUE INDEX professionals_idx
 ON professionals.Professionals
 ( UserId );

CREATE SEQUENCE professionals.endorsements_endorsementid_seq;

CREATE TABLE professionals.Endorsements (
                EndorsementId INTEGER NOT NULL DEFAULT nextval('professionals.endorsements_endorsementid_seq'),
                UserId INTEGER NOT NULL,
                ProfessionalId INTEGER NOT NULL,
                ProfessionId INTEGER NOT NULL,
                CONSTRAINT endorsementid PRIMARY KEY (EndorsementId)
);


ALTER SEQUENCE professionals.endorsements_endorsementid_seq OWNED BY professionals.Endorsements.EndorsementId;

CREATE SEQUENCE professionals.features_featureid_seq;

CREATE TABLE professionals.Features (
                FeatureId INTEGER NOT NULL DEFAULT nextval('professionals.features_featureid_seq'),
                ProfessionalId INTEGER NOT NULL,
                Icon VARCHAR NOT NULL,
                Headline VARCHAR NOT NULL,
                Description VARCHAR NOT NULL,
                CONSTRAINT featureid PRIMARY KEY (FeatureId)
);
COMMENT ON COLUMN professionals.Features.Icon IS 'Class of icon next to a feature';


ALTER SEQUENCE professionals.features_featureid_seq OWNED BY professionals.Features.FeatureId;

CREATE SEQUENCE professionals.imagesforprofessionals_imageid_seq;

CREATE TABLE professionals.ImagesForProfessionals (
                ImageId INTEGER NOT NULL DEFAULT nextval('professionals.imagesforprofessionals_imageid_seq'),
                ProfessionalId INTEGER NOT NULL,
                Path VARCHAR NOT NULL,
                Description VARCHAR,
                CONSTRAINT imageid PRIMARY KEY (ImageId)
);


ALTER SEQUENCE professionals.imagesforprofessionals_imageid_seq OWNED BY professionals.ImagesForProfessionals.ImageId;

CREATE SEQUENCE professionals.projects_projectid_seq;

CREATE TABLE professionals.Projects (
                ProjectId INTEGER NOT NULL DEFAULT nextval('professionals.projects_projectid_seq'),
                ProfessionalId INTEGER NOT NULL,
                UserId INTEGER,
                AddressId INTEGER,
                Name VARCHAR NOT NULL,
                LeadImage VARCHAR,
                ImageCount INTEGER DEFAULT 0 NOT NULL,
                Description VARCHAR,
                Cost INTEGER NOT NULL,
                Currency VARCHAR,
                DateStarted DATE,
                DatePerformed DATE,
                ProjectDuration INTEGER,
                LikesCount INTEGER DEFAULT 0 NOT NULL,
                CommentCount INTEGER DEFAULT 0 NOT NULL,
                MetaDescription VARCHAR(160) NOT NULL,
                PageUrl VARCHAR(80) NOT NULL,
                PageTitle VARCHAR(65) NOT NULL,
                CONSTRAINT projectid PRIMARY KEY (ProjectId)
);
COMMENT ON COLUMN professionals.Projects.Currency IS '€,Kn,$,KM';
COMMENT ON COLUMN professionals.Projects.ProjectDuration IS 'In days';
COMMENT ON COLUMN professionals.Projects.MetaDescription IS 'Meta description for search engines. ';
COMMENT ON COLUMN professionals.Projects.PageUrl IS 'User friendly professionals'' profile URL. Important for usability, link sharing and SEO.';
COMMENT ON COLUMN professionals.Projects.PageTitle IS 'The text that will appear in the browser''s title. Important for SEO reasons and usability.';


ALTER SEQUENCE professionals.projects_projectid_seq OWNED BY professionals.Projects.ProjectId;

CREATE TABLE professionals.ImagesForProjects (
                ProjectImageId INTEGER NOT NULL,
                ProjectId INTEGER NOT NULL,
                Path VARCHAR NOT NULL,
                ProjectStage VARCHAR,
                Description VARCHAR,
                CONSTRAINT projectimageid PRIMARY KEY (ProjectImageId)
);
COMMENT ON COLUMN professionals.ImagesForProjects.ProjectStage IS 'Before/After';


CREATE TABLE professionals.ItemsForProjects (
                ProjectId INTEGER NOT NULL,
                ItemId INTEGER NOT NULL,
                ServiceCost DOUBLE PRECISION,
                CostUnit VARCHAR,
                Description VARCHAR,
                CONSTRAINT itemforprojectid PRIMARY KEY (ProjectId, ItemId)
);


CREATE SEQUENCE professionals.testimonials_testimonialid_seq;

CREATE TABLE professionals.Testimonials (
                TestimonialId INTEGER NOT NULL DEFAULT nextval('professionals.testimonials_testimonialid_seq'),
                ProfessionalId INTEGER NOT NULL,
                ImagePath VARCHAR,
                Text VARCHAR NOT NULL,
                Summary VARCHAR NOT NULL,
                PersonName VARCHAR NOT NULL,
                PersonCompany VARCHAR,
                ProjectId INTEGER,
                Date DATE,
                OwnersReply VARCHAR,
                ReplyDate DATE,
                IsSelected BOOLEAN NOT NULL,
                CONSTRAINT testimonialid PRIMARY KEY (TestimonialId)
);
COMMENT ON COLUMN professionals.Testimonials.OwnersReply IS 'The reply of the professional who did the job';
COMMENT ON COLUMN professionals.Testimonials.IsSelected IS 'Selected testimonials appear at prominent places in the user interface of the application.';


ALTER SEQUENCE professionals.testimonials_testimonialid_seq OWNED BY professionals.Testimonials.TestimonialId;

CREATE SEQUENCE professionals.professionalsforcounties_countyid_seq;

CREATE TABLE professionals.ProfessionalsForCounties (
                ProfessionalId INTEGER NOT NULL,
                CountyId INTEGER NOT NULL DEFAULT nextval('professionals.professionalsforcounties_countyid_seq'),
                CONSTRAINT professionalforcountyid PRIMARY KEY (ProfessionalId, CountyId)
);
COMMENT ON TABLE professionals.ProfessionalsForCounties IS 'Connects professionals to counties they provide services in.';


ALTER SEQUENCE professionals.professionalsforcounties_countyid_seq OWNED BY professionals.ProfessionalsForCounties.CountyId;

CREATE SEQUENCE professionals.itemsforprofessionals_professionalid_seq;

CREATE SEQUENCE professionals.itemsforprofessionals_itemid_seq;

CREATE TABLE professionals.ItemsForProfessionals (
                ProfessionalId INTEGER NOT NULL DEFAULT nextval('professionals.itemsforprofessionals_professionalid_seq'),
                ItemId INTEGER NOT NULL DEFAULT nextval('professionals.itemsforprofessionals_itemid_seq'),
                ProfessionalDescription VARCHAR,
                CONSTRAINT itemsforprofessionalsid PRIMARY KEY (ProfessionalId, ItemId)
);


ALTER SEQUENCE professionals.itemsforprofessionals_professionalid_seq OWNED BY professionals.ItemsForProfessionals.ProfessionalId;

ALTER SEQUENCE professionals.itemsforprofessionals_itemid_seq OWNED BY professionals.ItemsForProfessionals.ItemId;

CREATE SEQUENCE professionals.employees_employeeid_seq;

CREATE TABLE professionals.Employees (
                EmployeeId INTEGER NOT NULL DEFAULT nextval('professionals.employees_employeeid_seq'),
                ProfessionalId INTEGER NOT NULL,
                Name VARCHAR NOT NULL,
                Image VARCHAR,
                Title VARCHAR,
                Role VARCHAR,
                CONSTRAINT employeeid PRIMARY KEY (EmployeeId)
);
COMMENT ON TABLE professionals.Employees IS 'Employee of a professional.';


ALTER SEQUENCE professionals.employees_employeeid_seq OWNED BY professionals.Employees.EmployeeId;

CREATE TABLE professionals.CommentsForProjects (
                CommentId INTEGER NOT NULL,
                ProjectId INTEGER NOT NULL,
                UserId INTEGER NOT NULL,
                Timestamp TIME NOT NULL,
                Content VARCHAR NOT NULL,
                CONSTRAINT commentid PRIMARY KEY (CommentId)
);


CREATE SEQUENCE professionals.properties_propertyid_seq;

CREATE TABLE professionals.Properties (
                PropertyId INTEGER NOT NULL DEFAULT nextval('professionals.properties_propertyid_seq'),
                PropertyType VARCHAR NOT NULL,
                Address INTEGER NOT NULL,
                UserId INTEGER NOT NULL,
                Size DOUBLE PRECISION NOT NULL,
                YearBuilt INTEGER NOT NULL,
                YearRenovated INTEGER NOT NULL,
                EnergyCertificate VARCHAR NOT NULL,
                Summary VARCHAR NOT NULL,
                Description VARCHAR NOT NULL,
                LeadImage VARCHAR NOT NULL,
                Permits VARCHAR NOT NULL,
                Images VARCHAR NOT NULL,
                GroundPlan VARCHAR NOT NULL,
                CONSTRAINT propertyid PRIMARY KEY (PropertyId)
);
COMMENT ON COLUMN professionals.Properties.Permits IS 'Vlasnički list, Građevinska, Uporabna';
COMMENT ON COLUMN professionals.Properties.Images IS 'List of image URLs. Best practice for images is to store them on the file system.';
COMMENT ON COLUMN professionals.Properties.GroundPlan IS 'Stored on file sytem as image';


ALTER SEQUENCE professionals.properties_propertyid_seq OWNED BY professionals.Properties.PropertyId;

ALTER TABLE professionals.Services ADD CONSTRAINT items_services_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ServiceCategories ADD CONSTRAINT items_servicecategory_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.TagsForItems ADD CONSTRAINT items_tagsforitems_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ItemsForProfessionals ADD CONSTRAINT items_itemsforprofessionals_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ItemsForProjects ADD CONSTRAINT items_itemsforprojects_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Professions ADD CONSTRAINT items_professions_fk
FOREIGN KEY (ItemId)
REFERENCES professionals.Items (ItemId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.TagsForItems ADD CONSTRAINT tags_tagsforitems_fk
FOREIGN KEY (TagId)
REFERENCES professionals.Tags (TagId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ServiceCategories ADD CONSTRAINT professions_servicecategory_fk
FOREIGN KEY (ProfessionId)
REFERENCES professionals.Professions (ProfessionId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Endorsements ADD CONSTRAINT professions_endorsements_fk
FOREIGN KEY (ProfessionId)
REFERENCES professionals.Professions (ProfessionId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Services ADD CONSTRAINT servicecategory_services_fk
FOREIGN KEY (ServiceCategoryId)
REFERENCES professionals.ServiceCategories (ServiceCategoryId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Districts ADD CONSTRAINT counties_districts_fk
FOREIGN KEY (CountyId)
REFERENCES professionals.Counties (CountyId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Localities ADD CONSTRAINT counties_localities_fk
FOREIGN KEY (CountyId)
REFERENCES professionals.Counties (CountyId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ProfessionalsForCounties ADD CONSTRAINT counties_professionalsforcounties_fk
FOREIGN KEY (CountyId)
REFERENCES professionals.Counties (CountyId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Localities ADD CONSTRAINT districts_localities_fk
FOREIGN KEY (DistrictId)
REFERENCES professionals.Districts (DistrictId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Addresses ADD CONSTRAINT localities_addresses_fk
FOREIGN KEY (LocalityId)
REFERENCES professionals.Localities (LocalityId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Properties ADD CONSTRAINT addresses_properties_fk
FOREIGN KEY (Address)
REFERENCES professionals.Addresses (AddressId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Users ADD CONSTRAINT addresses_users_fk
FOREIGN KEY (AddressId)
REFERENCES professionals.Addresses (AddressId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Properties ADD CONSTRAINT users_properties_fk
FOREIGN KEY (UserId)
REFERENCES professionals.Users (UserId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.CommentsForProjects ADD CONSTRAINT users_comments_fk
FOREIGN KEY (UserId)
REFERENCES professionals.Users (UserId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Professionals ADD CONSTRAINT users_professionals_fk
FOREIGN KEY (UserId)
REFERENCES professionals.Users (UserId)
ON DELETE CASCADE
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Projects ADD CONSTRAINT users_projects_fk
FOREIGN KEY (UserId)
REFERENCES professionals.Users (UserId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Endorsements ADD CONSTRAINT users_endorsements_fk
FOREIGN KEY (UserId)
REFERENCES professionals.Users (UserId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Employees ADD CONSTRAINT companies_professionals_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ItemsForProfessionals ADD CONSTRAINT professionals_itemsforprofessionals_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ProfessionalsForCounties ADD CONSTRAINT professionals_professionalsforcounties_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Testimonials ADD CONSTRAINT professionals_testimonials_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Projects ADD CONSTRAINT professionals_projects_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ImagesForProfessionals ADD CONSTRAINT professionals_imagesforprofessionals_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Features ADD CONSTRAINT professionals_features_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Endorsements ADD CONSTRAINT professionals_endorsements_fk
FOREIGN KEY (ProfessionalId)
REFERENCES professionals.Professionals (ProfessionalId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ItemsForProjects ADD CONSTRAINT projects_servicesforprojects_fk
FOREIGN KEY (ProjectId)
REFERENCES professionals.Projects (ProjectId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.ImagesForProjects ADD CONSTRAINT projects_imagesforprojects_fk
FOREIGN KEY (ProjectId)
REFERENCES professionals.Projects (ProjectId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.CommentsForProjects ADD CONSTRAINT projects_comments_fk
FOREIGN KEY (ProjectId)
REFERENCES professionals.Projects (ProjectId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE professionals.Testimonials ADD CONSTRAINT projects_testimonials_fk
FOREIGN KEY (ProjectId)
REFERENCES professionals.Projects (ProjectId)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

CREATE OR REPLACE FUNCTION professionals.string_to_bits(input_string character varying)
  RETURNS character varying AS
$BODY$
in_str = input_string
res_list = []
for c in in_str:
    bits = bin(ord(c))[2:]
    bits = '00000000'[len(bits):] + bits
    res_list.append(bits)
result = ''.join(res_list)

return result
$BODY$
  LANGUAGE plpython3u VOLATILE
  COST 100;
COMMENT ON FUNCTION professionals.string_to_bits(character varying) IS 'Returns bit representation of a string as string';



CREATE OR REPLACE FUNCTION professionals.id_or_default_sfun_text(_int_state text, _next_data_values text)
  RETURNS text AS
$BODY$

if _int_state:
   return "MULTIPLE_VALUES"
else:
   return _next_data_values

$BODY$
  LANGUAGE plpython3u VOLATILE
  COST 100;
COMMENT ON FUNCTION professionals.id_or_default_sfun_text(text, text) IS 'sfun function for id_or_default_text aggregate.';

CREATE AGGREGATE professionals.id_or_default_text(text) (
  SFUNC=professionals.id_or_default_sfun_text,
  STYPE=text
);

CREATE OR REPLACE FUNCTION professionals.id_or_default_sfun_num(_int_state DOUBLE PRECISION, _next_data_values DOUBLE PRECISION)
  RETURNS DOUBLE PRECISION AS
$BODY$

if _int_state:
   return -1
else:
   return _next_data_values

$BODY$
  LANGUAGE plpython3u VOLATILE
  COST 100;
COMMENT ON FUNCTION professionals.id_or_default_sfun_num(DOUBLE PRECISION, DOUBLE PRECISION) IS 'sfun function for id_or_default_num aggregate.';

CREATE AGGREGATE professionals.id_or_default_num(DOUBLE PRECISION) (
  SFUNC=professionals.id_or_default_sfun_num,
  STYPE=DOUBLE PRECISION
);

