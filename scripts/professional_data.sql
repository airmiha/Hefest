INSERT INTO professionals.users(username, password, email, mobile, telephone, agency) VALUES
('user1', 'user1', 'user1@users.com', '+38598123456', '+3851123456', true);
INSERT INTO professionals.addresses (lat, long, addressline, county, district) VALUES
(45.636386000000002, 16.121261000000001, 'Dolenec 51', 'Zagrebacka', 'Velika Gorica');
INSERT INTO professionals.professionals (professionalname, userid, addressid, status, score, projectcount, reviewcount, avgreview, verified, endorsementcount) VALUES 
('Vodoinstalater 1', (SELECT currval('professionals.users_userid_seq')), (SELECT currval('professionals.addresses_addressid_seq')), 'Active', 123, 4, 2, 4.3, true, 3);

INSERT INTO professionals.users(username, password, email, mobile, telephone, agency) VALUES
('user2', 'user2', 'user2@users.com', '+38598123456', '+3851123456', true);
INSERT INTO professionals.addresses (lat, long, addressline, county, district) VALUES
(45.641452999999998, 16.118217999999999, 'Turopoljska 52', 'Zagrebacka', 'Velika Gorica');
INSERT INTO professionals.professionals (professionalname, userid, addressid, status, score, projectcount, reviewcount, avgreview, verified, endorsementcount) VALUES 
('Stolar 1', (SELECT currval('professionals.users_userid_seq')), (SELECT currval('professionals.addresses_addressid_seq')), 'Active', 211, 5, 3, 4.7, true, 8);


INSERT INTO professionals.users(username, password, email, mobile, telephone, agency) VALUES
('user3', 'user3', 'user3@users.com', '+38598123456', '+3851123456', true);
INSERT INTO professionals.addresses (lat, long, addressline, county, district) VALUES
(45.633336, 16.113206000000002, 'Vinogradska 12', 'Zagrebacka', 'Velika Gorica');
INSERT INTO professionals.professionals (professionalname, userid, addressid, status, score, projectcount, reviewcount, avgreview, verified, endorsementcount) VALUES 
('Električar 4', (SELECT currval('professionals.users_userid_seq')), (SELECT currval('professionals.addresses_addressid_seq')), 'Active', 87, 2, 1, 3.5, true, 1);
