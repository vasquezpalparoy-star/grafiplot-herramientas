create table public.grafiplot_identity_links(legacy_uid text primary key,new_uid text not null unique,created_at timestamptz not null default now());
alter table public.grafiplot_identity_links enable row level security;
revoke all on public.grafiplot_identity_links from public,anon,authenticated;
grant all on public.grafiplot_identity_links to service_role;
create policy identity_links_server_only on public.grafiplot_identity_links for all to anon,authenticated using(false) with check(false);
create function public.grafiplot_migrate_identity(p_old text,p_new text) returns jsonb language plpgsql security invoker set search_path='' as $$
declare linked text;
begin
 if p_old=p_new or p_new not like 'publicidad:%' or length(p_new)>128 then raise exception 'invalid_identity';end if;
 perform pg_advisory_xact_lock(hashtextextended(least(p_old,p_new),0));
 perform pg_advisory_xact_lock(hashtextextended(greatest(p_old,p_new),0));
 select new_uid into linked from public.grafiplot_identity_links where legacy_uid=p_old;
 if found then
  if linked<>p_new then raise exception 'identity_already_linked';end if;
  return jsonb_build_object('migrated',true,'alreadyLinked',true,'uid',p_old);
 end if;
 if exists(select 1 from public.grafiplot_conversations where client_id=p_new) then raise exception 'target_conversation_exists';end if;
 insert into public.grafiplot_identity_links(legacy_uid,new_uid) values(p_old,p_new);
 return jsonb_build_object('migrated',true,'uid',p_old);
end $$;
revoke all on function public.grafiplot_migrate_identity(text,text) from public,anon,authenticated;
grant execute on function public.grafiplot_migrate_identity(text,text) to service_role;
