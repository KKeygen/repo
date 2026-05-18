local operate_order_status = tonumber(KEYS[1])
local program_id = KEYS[2]
local un_lock_seat_id_json_array = cjson.decode(ARGV[1])
local add_seat_data_json_array = cjson.decode(ARGV[2])
for index, un_lock_seat_id_json_object in pairs(un_lock_seat_id_json_array) do
    local program_seat_hash_key = un_lock_seat_id_json_object.programSeatLockHashKey
    local un_lock_seat_id_list = un_lock_seat_id_json_object.unLockSeatIdList
    if (#un_lock_seat_id_list > 0) then
        redis.call('HDEL',program_seat_hash_key,unpack(un_lock_seat_id_list))
    end
end

for index, add_seat_data_json_object in pairs(add_seat_data_json_array) do
    local seat_hash_key_add = add_seat_data_json_object.seatHashKeyAdd
    local seat_data_list = add_seat_data_json_object.seatDataList
    if (#seat_data_list > 0) then
        redis.call('HMSET',seat_hash_key_add,unpack(seat_data_list))
    end
end

if (operate_order_status == 2) then
    local ticket_category_list = cjson.decode(ARGV[3])
    for index,increase_data in ipairs(ticket_category_list) do
        local program_ticket_remain_number_hash_key = increase_data.programTicketRemainNumberHashKey
        local ticket_category_id = increase_data.ticketCategoryId
        local increase_count = increase_data.count
        redis.call('HINCRBY',program_ticket_remain_number_hash_key,ticket_category_id,increase_count)
    end
    if ARGV[4] and ARGV[4] ~= "" then
        local id_number_list = cjson.decode(ARGV[4])
        if (#id_number_list > 0) then
            local program_id_card_hash_key = "PROGRAM_ID_CARD:" .. tostring(program_id)
            redis.call('HDEL',program_id_card_hash_key,unpack(id_number_list))
        end
    end
end
