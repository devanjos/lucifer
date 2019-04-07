UPDATE presentation pr
	INNER JOIN product pd ON pd.id = pr.product_id
	INNER JOIN drug d ON d.id = pd.id
SET pr.manual_price = true
WHERE pr.code IN('99999999999');