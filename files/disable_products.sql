UPDATE presentation pr
	INNER JOIN product pd ON pd.id = pr.id
	INNER JOIN drug d ON d.id = pd.id
	INNER JOIN supplier s ON s.id = pd.supplier_id
SET pr.enabled = false
WHERE pr.price_anjos IS NULL
	OR pr.price_anjos <= 0
	OR d.prescription = true
#	OR s.name NOT IN()
#	OR pr.code NOT IN()
;