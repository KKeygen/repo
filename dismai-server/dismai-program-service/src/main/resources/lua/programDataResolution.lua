local ticket_category_list = cjson.decode(ARGV[1])
local del_seat_list = cjson.decode(ARGV[2])
local add_seat_data_list = cjson.decode(ARGV[3])
local total_remain_list = {}
if ARGV[4] and ARGV[4] ~= "" then
    total_remain_list = cjson.decode(ARGV[4])
end
local id_number_list = {}
if ARGV[5] and ARGV[5] ~= "" then
    id_number_list = cjson.decode(ARGV[5])
end

for index,increase_data in ipairs(ticket_category_list) do
    local program_ticket_remain_number_hash_key = increase_data.programTicketRemainNumberHashKey
    local ticket_category_id = increase_data.ticketCategoryId
    local increase_count = increase_data.count
    redis.call('HINCRBY',program_ticket_remain_number_hash_key,ticket_category_id,increase_count)
end
for index, seat in pairs(del_seat_list) do
    local seat_hash_key_del = seat.seatHashKeyDel
    local seat_id_list = seat.seatIdList
    if next(seat_id_list) ~= nil then
        redis.call('HDEL',seat_hash_key_del,unpack(seat_id_list))
    end
end
for index, seat in pairs(add_seat_data_list) do
    local seat_hash_key_add = seat.seatHashKeyAdd
    local seat_data_list = seat.seatDataList
    if next(seat_data_list) ~= nil then
        redis.call('HMSET',seat_hash_key_add,unpack(seat_data_list))
    end
end
for index,total_remain_data in ipairs(total_remain_list) do
    local program_ticket_total_remain_key = total_remain_data.programTicketTotalRemainKey
    local increase_count = total_remain_data.count
    redis.call('INCRBY',program_ticket_total_remain_key,increase_count)
end
if KEYS[2] and KEYS[2] ~= "" and next(id_number_list) ~= nil then
    local program_id_card_hash_key = "PROGRAM_ID_CARD:" .. tostring(KEYS[2])
    redis.call('HDEL',program_id_card_hash_key,unpack(id_number_list))
end
