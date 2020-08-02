// Headers, Params, etc
export const HEADER_IF_MATCH = 'If-Match';
export const HEADER_IF_NONE_MATCH = 'If-None-Match';

// Validators
export const NAME_REGEX = /^[\w-'. ]{2,50}$/;
export const USER_ID_REGEX = /^[\w-]{3,12}$/;

// Game
export const DICE_COUNT = 2;
export const INIT_BANK_RESOURCE_COUNT = 19;
export const MAX_ROLL_PER_DIE = 6;
export const MIN_ROLL_PER_DIE = 1;

// Error Messages
export const DB_ERROR_BAD_STATUS_CODE = 'DB [%s] error - bad status code [%d]';
export const DB_ERROR_MISSING_RESOURCE = 'DB [%s] error - Missing resource in response';
export const DB_ERROR_UNKNOWN = 'DB [%s] error - unknown';
export const ENTITY_CONFLICT = 'Entity [%s] with id [%s] already exists';
export const ENTITY_NOT_FOUND = 'Entity [%s] with id [%s] not found';
export const ENTITY_PRECONDITION_FAILED = 'Entity [%s] with id [%s] does not exist';

// Http REST Methods
export const METHOD_GET = 'GET';
export const METHOD_POST = 'POST';
export const METHOD_PUT = 'PUT';
export const METHOD_PATCH = 'PATCH';
export const METHOD_DELETE = 'DELETE';

// Misc
export const BEARER_PREFIX = 'Bearer ';
export const DAY_MILLIS = 24 * 60 * 60 * 1000;
