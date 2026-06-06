ALTER TABLE dismai_order_0.d_order_ticket_user_0 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_0.d_order_ticket_user_1 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_0.d_order_ticket_user_2 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_0.d_order_ticket_user_3 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_1.d_order_ticket_user_0 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_1.d_order_ticket_user_1 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_1.d_order_ticket_user_2 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;
ALTER TABLE dismai_order_1.d_order_ticket_user_3 ADD COLUMN id_number varchar(512) DEFAULT NULL COMMENT '购票人证件号码' AFTER ticket_user_id;

ALTER TABLE dismai_order_0.d_order_0 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_0.d_order_1 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_0.d_order_2 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_0.d_order_3 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_1.d_order_0 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_1.d_order_1 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_1.d_order_2 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';
ALTER TABLE dismai_order_1.d_order_3 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格';

ALTER TABLE dismai_order_0.d_order_ticket_user_0 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_0.d_order_ticket_user_1 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_0.d_order_ticket_user_2 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_0.d_order_ticket_user_3 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_1.d_order_ticket_user_0 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_1.d_order_ticket_user_1 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_1.d_order_ticket_user_2 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';
ALTER TABLE dismai_order_1.d_order_ticket_user_3 MODIFY order_price decimal(10,2) DEFAULT NULL COMMENT '订单价格', MODIFY pay_order_price decimal(10,2) DEFAULT NULL COMMENT '支付订单价格';

ALTER TABLE dismai_pay_0.d_pay_bill_0 MODIFY pay_amount decimal(10,2) NOT NULL COMMENT '支付金额';
ALTER TABLE dismai_pay_0.d_pay_bill_1 MODIFY pay_amount decimal(10,2) NOT NULL COMMENT '支付金额';
ALTER TABLE dismai_pay_1.d_pay_bill_0 MODIFY pay_amount decimal(10,2) NOT NULL COMMENT '支付金额';
ALTER TABLE dismai_pay_1.d_pay_bill_1 MODIFY pay_amount decimal(10,2) NOT NULL COMMENT '支付金额';
ALTER TABLE dismai_pay_0.d_refund_bill_0 MODIFY refund_amount decimal(10,2) NOT NULL COMMENT '退款金额';
ALTER TABLE dismai_pay_0.d_refund_bill_1 MODIFY refund_amount decimal(10,2) NOT NULL COMMENT '退款金额';
ALTER TABLE dismai_pay_1.d_refund_bill_0 MODIFY refund_amount decimal(10,2) NOT NULL COMMENT '退款金额';
ALTER TABLE dismai_pay_1.d_refund_bill_1 MODIFY refund_amount decimal(10,2) NOT NULL COMMENT '退款金额';

ALTER TABLE dismai_program_0.d_seat_0 MODIFY price decimal(10,2) NOT NULL COMMENT '座位价格';
ALTER TABLE dismai_program_0.d_seat_1 MODIFY price decimal(10,2) NOT NULL COMMENT '座位价格';
ALTER TABLE dismai_program_1.d_seat_0 MODIFY price decimal(10,2) NOT NULL COMMENT '座位价格';
ALTER TABLE dismai_program_1.d_seat_1 MODIFY price decimal(10,2) NOT NULL COMMENT '座位价格';
ALTER TABLE dismai_program_0.d_ticket_category_0 MODIFY price decimal(10,2) NOT NULL COMMENT '价格';
ALTER TABLE dismai_program_0.d_ticket_category_1 MODIFY price decimal(10,2) NOT NULL COMMENT '价格';
ALTER TABLE dismai_program_1.d_ticket_category_0 MODIFY price decimal(10,2) NOT NULL COMMENT '价格';
ALTER TABLE dismai_program_1.d_ticket_category_1 MODIFY price decimal(10,2) NOT NULL COMMENT '价格';
