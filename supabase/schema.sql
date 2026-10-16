-- Proyecto dedicado Grafiplot. Acceso de datos exclusivamente desde el servidor autenticado.
create table public.grafiplot_admins(email text primary key, active boolean not null default true);
insert into public.grafiplot_admins(email) values ('vasquezpalparoy@gmail.com');
create table public.grafiplot_conversations(
 client_id text primary key check(length(client_id) between 1 and 128),
 client_name text not null check(length(client_name) between 1 and 80),
 last_message text not null default '', updated_at timestamptz not null default now()
);
create index grafiplot_conversations_recent on public.grafiplot_conversations(updated_at desc);
create table public.grafiplot_messages(
 id uuid primary key, conversation_id text not null references public.grafiplot_conversations(client_id),
 sender_id text not null, sender_role text not null check(sender_role in ('client','admin')),
 body text not null check(length(trim(body)) between 1 and 4000),created_at timestamptz not null default now()
);
create index grafiplot_messages_thread on public.grafiplot_messages(conversation_id,created_at desc);
create index grafiplot_messages_sender on public.grafiplot_messages(sender_id,created_at desc);
create table public.grafiplot_devices(
 token_hash text primary key, uid text not null, is_admin boolean not null default false, admin_email text,
 token text not null check(length(token) between 30 and 4096),updated_at timestamptz not null default now()
);
create index grafiplot_devices_user on public.grafiplot_devices(uid);
create table public.grafiplot_announcements(
 id uuid primary key, title text not null, body text not null,style text not null,
 target text not null,image_url text not null default '',status text not null default 'pending',
 created_at timestamptz not null default now(),author_uid text not null
);
create index grafiplot_announcements_recent on public.grafiplot_announcements(created_at desc);
alter table public.grafiplot_admins enable row level security;
alter table public.grafiplot_conversations enable row level security;
alter table public.grafiplot_messages enable row level security;
alter table public.grafiplot_devices enable row level security;
alter table public.grafiplot_announcements enable row level security;
revoke all on public.grafiplot_admins,public.grafiplot_conversations,public.grafiplot_messages,public.grafiplot_devices,public.grafiplot_announcements from anon,authenticated;
grant all on public.grafiplot_admins,public.grafiplot_conversations,public.grafiplot_messages,public.grafiplot_devices,public.grafiplot_announcements to service_role;

create function public.grafiplot_send_message(p_id uuid,p_uid text,p_conversation text,p_name text,p_role text,p_body text)
returns jsonb language plpgsql security invoker set search_path = '' as $$
declare old public.grafiplot_messages; result public.grafiplot_messages;
begin
 if p_role not in ('client','admin') or (p_role='client' and p_uid<>p_conversation) then raise exception 'forbidden';end if;
 perform pg_advisory_xact_lock(hashtextextended(p_uid,0));
 select * into old from public.grafiplot_messages where id=p_id;
 if found then
  if old.sender_id<>p_uid or old.conversation_id<>p_conversation or old.body<>p_body then raise exception 'id_conflict';end if;
  return jsonb_build_object('message',to_jsonb(old),'duplicate',true);
 end if;
 if (select count(*) from public.grafiplot_messages where sender_id=p_uid and created_at>now()-interval '1 minute')>=30 then raise exception 'rate_limit';end if;
 if p_role='client' then
  insert into public.grafiplot_conversations(client_id,client_name) values(p_uid,left(p_name,80)) on conflict(client_id) do nothing;
 elsif not exists(select 1 from public.grafiplot_conversations where client_id=p_conversation) then raise exception 'conversation_missing';end if;
 insert into public.grafiplot_messages(id,conversation_id,sender_id,sender_role,body) values(p_id,p_conversation,p_uid,p_role,trim(p_body)) returning * into result;
 update public.grafiplot_conversations set last_message=left(trim(p_body),200),updated_at=result.created_at where client_id=p_conversation;
 return jsonb_build_object('message',to_jsonb(result),'duplicate',false);
end $$;
revoke all on function public.grafiplot_send_message(uuid,text,text,text,text,text) from public,anon,authenticated;
grant execute on function public.grafiplot_send_message(uuid,text,text,text,text,text) to service_role;

create policy grafiplot_admins_no_direct_access on public.grafiplot_admins for all to anon,authenticated using(false) with check(false);
create policy grafiplot_conversations_no_direct_access on public.grafiplot_conversations for all to anon,authenticated using(false) with check(false);
create policy grafiplot_messages_no_direct_access on public.grafiplot_messages for all to anon,authenticated using(false) with check(false);
create policy grafiplot_devices_no_direct_access on public.grafiplot_devices for all to anon,authenticated using(false) with check(false);
create policy grafiplot_announcements_no_direct_access on public.grafiplot_announcements for all to anon,authenticated using(false) with check(false);
